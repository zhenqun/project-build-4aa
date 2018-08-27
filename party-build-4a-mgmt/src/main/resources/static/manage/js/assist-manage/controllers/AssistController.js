(function (angular) {
    angular.module('mgnt')
        .config(function(alertProvider) {
            'ngInject';

            alertProvider.setDefaultOption({
                dialog: true
            });
        })
        .controller('AssistController', function($scope, $q, $uibModal, toastrService, alert, assistManageService, exportUsersService, DEFAULT_PAGESIZE, MODAL_DIALOG_CONFIGS, FLAGS) {
            'ngInject';

            $scope.isLoading = false;
            $scope.queryParams = {
                relName: '',
                idCard: '',
                telephone: '',
                state: '',
                isActive: '',
                pageNo: 1,
                pageSize: DEFAULT_PAGESIZE
            };
            $scope.params = {};

            $scope.list = [];
            $scope.totalItems = 0;

            this.$onInit = function() {
                $scope.applyParams();

                $scope.query();
            };

            /* 将界面上用户选择/输入的查询参数同步到生效的参数中 */
            $scope.applyParams = function() {
                $scope.params = angular.extend({}, $scope.queryParams);
            };

            $scope.beforeQuery = function() {
                $scope.queryParams.pageNo = 1;
                $scope.applyParams();

                $scope.query();
            };

            $scope.query = function() {
                if ($scope.isLoading) {
                    return;
                }

                $scope.isLoading = true;
                assistManageService.query($scope.params)
                    .then(function(resp) {
                        $scope.isLoading = false;
                        var result  = resp.data;
                        if (result != null) {
                            $scope.totalItems = result.count;
                            $scope.list = result.data;
                        }
                        else {
                            $scope.totalItems = 0;
                            $scope.list = [];
                        }
                    })
                    .catch(function(error) {
                        $scope.isLoading = false;
                        toastrService.error(error);

                        $scope.totalItems = 0;
                        $scope.list = [];
                    });
            };

            $scope.openAssistor = function() {
                $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                    templateUrl: 'open-assist-modal.html',
                    controller: 'OpenAssistorController',
                    size: 'lg'
                })).result.then(function() {
                    $scope.query();
                });
            };

            $scope.openBatchAdd = function() {
                $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                    templateUrl: 'batch-open-modal.html',
                    controller: 'UploadAssistorController'
                })).result.then(function(result) {
                    var assistors = result.assistors;
                    var ignoredCount = result.rawUsers.length - assistors.length;
                    if (assistors != null) {
                        assistors.forEach(function(x) {
                            if (x.telephone) {
                                x.telephone = x.telephone.toString();
                            }
                        });
                    }
                    return $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                        templateUrl: 'batch-add-assistor.html',
                        controller: 'BatchAddAssistorController',
                        size: 'lg',
                        resolve: {
                            batch: function() {
                                return {
                                    assistors: assistors,
                                    ignoredCount: ignoredCount
                                };
                            }
                        }
                    })).result;
                }).then(function() {
                    $scope.query();
                });
            };

            $scope.resetVpnPassword = function(user) {
                alert({
                    content: '确定要将 ' + user.relName + ' 的 VPN 密码重置为身份证号后六位吗？该操作不可恢复，请谨慎操作。'
                })
                .then(function() {
                    return assistManageService.resetVpn(user);
                })
                .then(function(resp) {
                    var result = resp.data;
                    if (result) {
                        toastrService.success('重置 VPN 密码成功，下次登录 VPN 请使用新密码');
                        return;
                    }
                    return $q.reject('重置 VPN 密码失败，请稍后重试');
                })
                .catch(function(error) {
                    toastrService.error(error);
                });
            };

            function operateFinished(resp) {
                var result = resp.data;
                if (result.flag === FLAGS.Success) {
                    toastrService.success('操作成功');
                    $scope.query();
                }
                else {
                    toastrService.error(result.message);
                }
            }

            // 0 禁用状态 1 启用状态
            $scope.setState = function(state, user) {
                var states = {
                    0: '禁用',
                    1: '启用'
                };

                var assistorIds = [];
                var stateText = states[state];
                var confirmText = '';
                if (!user) {
                    var assistors = $scope.list.filter(function(x) { return x._selected; });
                    if (assistors.length === 0) {
                        toastrService.warning('请选择辅助安全员');
                        return;
                    }
                    assistorIds = assistors.map(function(x) { return x.fzuserId; });
                    confirmText = '确定要' + stateText + '选中的 ' + assistorIds.length + ' 名辅助安全员吗？';
                }
                else {
                    assistorIds = [user.fzuserId];
                    confirmText = '确定要' + stateText + '辅助安全员 ' + user.relName + ' 吗？';
                }

                if (state === 0) {
                    confirmText += '禁用后将无法登录系统发起和查看申请。';
                }

                return alert({
                    content: confirmText
                }).then(function() {
                    return assistManageService.setState({
                        fzuserIds: assistorIds,
                        state: state
                    });
                }).then(operateFinished);
            };

            $scope.revokeAssistor = function(assistor) {
                var assistorIds = null;
                var confirmText = '';
                if (assistor) {
                    assistorIds = [assistor.fzuserId];
                    confirmText = '确定要撤销辅助安全员 ' + assistor.relName + ' 吗？';
                }
                else {
                    var assistors = $scope.list.filter(function(x) { return x._selected; });
                    if (assistors.length === 0) {
                        toastrService.warning('请选择辅助安全员');
                        return;
                    }
                    assistorIds = assistors.map(function(x) { return x.fzuserId; });
                    confirmText = '确定要撤销选中的 ' + assistorIds.length + ' 名辅助安全员吗？';
                }
                confirmText += '撤销后辅助安全员的账号、VPN 账号等所有信息会被删除。';

                alert({
                    content: confirmText
                }).then(function() {
                    return assistManageService.revoke(assistorIds);
                }).then(operateFinished);
            };

            $scope.reauthorize = function(assistor) {
                var assistors = null;
                if (assistor) {
                    assistors = [assistor];
                }
                else {
                    assistors = $scope.list.filter(function(x) { return x._selected; });
                    if (assistors.length === 0) {
                        toastrService.warning('请选择辅助安全员');
                        return;
                    }
                }

                $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                    templateUrl: 'reauthorize-modal.html',
                    controller: 'AssistorReauthorizeController',
                    size: 'lg',
                    resolve: {
                        assistors: function() {
                            return angular.copy(assistors);
                        }
                    }
                })).result.then(function() {
                    $scope.query();
                });
            };

            $scope.exportAuthorizeCodes = function() {
                const assistors = $scope.list.filter(x => x._selected);
                if (assistors.length === 0) {
                    toastrService.warning('请选择辅助安全员');
                    return;
                }
                const ids = assistors.map(x => x.fzuserId);
                return exportUsersService.exportUser('/assistSecurity/exportAuthorizationCode', ids);
            };
        });
})(angular);
