(function (angular) {
    angular.module('mgnt')
        .controller('SecuController', [
            '$scope',
            '$window',
            '$timeout',
            '$rootScope',
            '$http',
            '$q',
            '$uibModal',
            'toastrService',
            'Upload',
            'exportUsersService',
            'alert',
            'topOrgService',
            'DEFAULT_PAGESIZE',
            'ORG_LEVELS',
            'MODAL_DIALOG_CONFIGS',
            function ($scope, $window, $timeout, $rootScope, $http, $q, $uibModal, toastrService, Upload,
                      exportUsersService, alert, topOrgService, DEFAULT_PAGESIZE, ORG_LEVELS, MODAL_DIALOG_CONFIGS) {
                'ngInject';

                $scope.ORG_LEVELS = ORG_LEVELS;
                $scope.contentLength = 0;
                this.$onInit = function () {
                    // $scope.queryUsers();
                    // $scope.queryClients();
                }
                $scope.totalItems = '';
                $scope.orgId = '';
                $scope.orgInfo = {
                    manageOrgName: '',
                    manageOrgCode: ''
                };
                $scope.page = {
                    pageNo: 1,
                    pageSize: DEFAULT_PAGESIZE
                };
                $scope.importPage = {
                    pageNo: 1,
                    pageSize: DEFAULT_PAGESIZE
                };
                $scope.importTotalItems = '';
                $scope.isLoading = false;
                $scope.totalItems = '';
                $scope.queryParams = {
                    manageOrgId: '',
                    state: '',
                    securityUserName: '',
                    isActivation: ''
                };

                $scope.userList = [];

                $(window).on('load', function() {
                    $('.modal-content').click(function () {
                        return false;
                    });
                });

                //query data when app changes 
                $rootScope.$watch('selectClient.clientId ', function (newval, oldval) {
                    if (newval == oldval) {
                        return;
                    }
                    $timeout(function () {
                        // $rootScope.selectClient = $scope.appList.find(function (x) { return x.clientId == $scope.queryParams.clientId });
                        if (!$rootScope.clients.isLoading) {
                            $scope.orgUrl = $rootScope.selectClient.classificationUrl;
                            $scope.queryParams.clientId = $rootScope.selectClient.clientId;
                            $scope.queryParams.manageOrgId = '';
                            $scope.currentAppName = $rootScope.selectClient.clientName;
                            //首次加载显示顶层组织
                            $scope.userList = [];
                            $scope.orgLoading = true;
                            $scope.queryTopOrgClass($scope.queryParams.clientId);
                            $scope.otherAppList = $scope.appList.filter(function (x) {
                                return x.clientId != newval;
                            })
                        }
                    })
                });



                //返回操作结果并提示foo
                var operation = function (data) {
                    var data = data.data;
                    if (data.flag == 'success') {
                        toastrService.success('操作成功');
                        $scope.queryUsers();
                    }
                    else {
                        toastrService.error(data.message);
                    }
                };



                //开通后打开分配窗口
                var addSuccessHandler = function (params, data) {
                    $('#modal-4').modal('hide');
                    $('#modal-7').modal('hide');
                    // 打开分配窗口
                    var modalInstance = $uibModal.open({
                        templateUrl: 'dist.html',
                        controller: 'DistController',
                        animation: true,
                        resolve: {
                            userIds: function () {
                                return {
                                    securityUsers: data.message.split(',').map(function (x) {
                                        return {
                                            securityUserId: x.split("|")[0],
                                            securityUserName: x.split("|")[1]
                                        }
                                    }),
                                    selectClientId: $rootScope.selectClient.clientId

                                }
                            }
                        }
                    });
                    modalInstance.result.then(function () {
                        $scope.queryUsers();
                    }, function () { });

                }

                $scope.queryUsers = function () {
                    $scope.isLoading = true;
                    var queryParams = angular.merge({}, $scope.queryParams, $scope.page)
                    queryParams.manageOrgName = $scope.orgInfo.name;
                    queryParams.manageOrgCode = $scope.orgInfo.code;
                    $http.post('/manage/security/securityUserQuery', queryParams)
                        .then(function (data) {
                            var data = data.data;
                            $scope.isLoading = false;
                            if (data) {
                                $scope.userList = data.data || [];

                                $scope.totalItems = data.count || 0;
                            }
                            // else {
                            //     toastrService.error('');
                            // }
                        })
                };

                //注销
                $scope.logout = function (userId) {
                    var securityUsers = $scope.userList.filter(function (x) { return x._selected })
                    if (!userId) {
                        if (securityUsers.length == 0) {
                            toastrService.warning('请选择安全员');
                            return;
                        }
                        var securityUserIds = securityUsers.map(function (x) { return x.securityUserId });
                    }
                    else {
                        var securityUserIds = [userId];
                    }
                    return alert({
                        content: '您确定要注销吗？'
                    }).then(function(data) {
                        $http.post('/manage/security/cancellationSecurityUser', { securityUserIds: securityUserIds })
                            .then(operation);
                    });
                };

                //0 禁用状态 1启用状态
                $scope.state = function (state, userId) {
                    if (!userId) {
                        var securityUsers = $scope.userList.filter(function (x) { return x._selected })
                        if (securityUsers.length == 0) {
                            toastrService.warning('请选择安全员');
                            return;
                        }
                        var securityUserIds = securityUsers.map(function (x) { return x.securityUserId });
                    }
                    else {
                        var securityUserIds = [userId];
                    }
                    var tips = '';
                    if (state == 0) {
                        tips = '您确定要启用吗？'
                    }
                    else {
                        tips = '您确定要禁用吗？'
                    }
                    return alert({
                        content: tips
                    }).then(function(data) {
                        $http.post('/manage/security/modifySecurityUserState', { securityUserIds: securityUserIds, state: state })
                            .then(operation);
                    });

                };
                //打开分配安全员
                $scope.openDist = function (user) {
                    var securityUsers = $scope.userList.filter(function (x) { return x._selected })
                    if (!user && securityUsers.length == 0) {
                        toastrService.warning('请选择安全员');
                        return;
                    }
                    if (!user) {
                        // var securityUserIds = securityUsers.map(function (x) { return x.securityUserId });
                    }
                    else {
                        securityUsers = [user];
                    }
                    var modalInstance = $uibModal.open({
                        templateUrl: 'dist.html',
                        controller: 'DistController',
                        animation: true,
                        resolve: {
                            userIds: function () {
                                return {
                                    securityUsers: securityUsers,
                                    selectClientId: $rootScope.selectClient.clientId
                                }
                            }
                        }
                    });
                    modalInstance.result.then(function () {
                        $scope.queryUsers();
                    }, function () { });
                };
                //批量导入，现有逻辑不支持批量，需要重新实现 file.upload.then 内部的逻辑
                $scope.uploadFiles = function (file, errFiles) {
                    $scope.f = file;
                    $scope.errFile = errFiles && errFiles[0];
                    if (file) {
                        file.upload = Upload.upload({
                            url: '/exportUserExcl',
                            data: { file: file }
                        });

                        file.upload.then(function (response) {
                            $timeout(function () {
                                file.result = response.data;
                                result = response.data;
                                if (result) {
                                    $scope.allImportList = result;
                                    $scope.importTotalItems = result.length;
                                    $scope.pageData(result);
                                    $('#modal-7').modal('show');
                                }
                            });
                        }, function (response) {
                            if (response.status > 0)
                                $scope.errorMsg = response.status + ': ' + response.data;
                        }, function (evt) {
                            file.progress = Math.min(100, parseInt(100.0 *
                                evt.loaded / evt.total));
                        });
                    }
                };
                //导入信息分页
                $scope.pageData = function () {
                    var temp = angular.copy($scope.allImportList);
                    // var DEFAULT_PAGESIZE = 2;
                    var pageItem = DEFAULT_PAGESIZE * ($scope.importPage.pageNo - 1);
                    if (temp.length > DEFAULT_PAGESIZE) {
                        $scope.importList = temp.splice(pageItem, DEFAULT_PAGESIZE);
                    }
                    else {
                        $scope.importList = temp;
                    }
                }
                $scope.closeModal = function (ele) {
                    $(ele).modal('hide');
                };
                // 导出
                $scope.exportUser = function () {
                    var securityUsers = $scope.userList.filter(function (x) { return x._selected });
                    if (securityUsers.length == 0) {
                        toastrService.warning('请选择安全员');
                        return;
                    }
                    var ids = securityUsers.map(function (x) { return x.securityUserId });
                    return exportUsersService.exportUser('/manage/security/exportAuthorizationCode', ids);
                };

                //首次加载显示顶层组织
                $scope.queryTopOrgClass = topOrgService.initTopOrg(function (data) {
                    $scope.orgLoading = false;
                    $scope.topOrg = data.data[0];
                    $scope.queryParams.manageOrgId = $scope.topOrg.orgId;
                    $scope.displayOrg = $scope.topOrg.orgName;
                    $scope.queryUsers();
                });

                $scope.openSecu = function() {
                    var modalInstance = $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                        templateUrl: 'open-secu.html',
                        controller: 'OpenSecuController',
                        size: 'lg'
                    }));
                    modalInstance.result.then(function() {
                        $scope.queryUsers();
                    });
                };

                $scope.clearClient = function(user) {
                    var users = [];
                    if (user == null) {
                        users = $scope.userList.filter(function(x) { return x._selected; });
                    }
                    else {
                        users = [user];
                    }
                    users = users.filter(function(x) { return x; });
                    if (users.length === 0) {
                        toastrService.warning('请选择安全员');
                        return;
                    }

                    $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                        templateUrl: 'clear-client-modal.html',
                        controller: 'ClearClientController',
                        resolve: {
                            type: function() { return 'security'; },
                            client: function() { return $rootScope.selectClient; },
                            selectedUsers: function() {
                                return users.map(function(x) {
                                    var user = angular.extend({}, x, {
                                        id: x.securityUserId,
                                        name: x.securityUserName
                                    });
                                    return user;
                                });
                            }
                        }
                    })).result.then(function() {
                        $scope.queryUsers();
                    });
                };

                $scope.resetVpnPassword = function(user) {
                    if (user == null) {
                        toastrService.warning('请选择安全员');
                        return;
                    }
                    alert({
                        content: '确定要将 ' + user.securityUserName + ' 的 VPN 密码重置为身份证号后六位吗？该操作不可恢复，请谨慎操作。'
                    })
                    .then(function() {
                        return $http.post('/manage/security/resetVpnPassword/' + user.securityUserId);
                    })
                    .then(function(resp) {
                        var data = resp.data;
                        if (data) {
                            toastrService.success('重置 VPN 密码成功，下次登录 VPN 请使用新密码');
                            return;
                        }
                        return $q.reject('重置 VPN 密码失败，请稍后重试');
                    })
                    .catch(function(error) {
                        toastrService.error(error);
                    });
                };
            }
        ])
        .controller('DistController', [
            '$scope',
            '$window',
            '$http',
            '$timeout',
            '$uibModalInstance',
            'toastrService',
            'userIds',
            function ($scope, $window, $http, $timeout, $uibModalInstance, toastrService, userIds) {
                'ngInject';

                this.$onInit = function () {
                    $scope.securityUsers = userIds.securityUsers;
                    $scope.selectClientId = userIds.selectClientId;
                }
                $scope.orgId = '';
                $scope.isSubmitting = false;
                $scope.manageOrgId = '';
                $scope.orgInfo = {
                    name: '',
                    code: ''
                };

                $scope.dist = function () {
                    if ($scope.isSubmitting) {
                        return;
                    }

                    var params = $scope.securityUsers.map(function (x) {
                        var data = {
                            manageOrgId: x.manageId,
                            securityUserId: x.securityUserId,
                            clientId: $scope.selectClientId
                        };
                        var orgInfo = x.orgInfo;
                        if (orgInfo != null) {
                            data.manageOrgName = orgInfo.name;
                            data.manageOrgCode = orgInfo.code;
                            var extData = orgInfo.extData;
                            if (extData != null) {
                                data.ouName = extData.ouName;
                            }
                        }

                        return data;
                    });

                    var hasNotOrg = params.some(function(x) {
                        return (!x.manageOrgId) || (!x.manageOrgName) || (!x.manageOrgCode);
                    });
                    if (hasNotOrg) {
                        toastrService.warning('请重新选择组织');
                        return;
                    }

                    $scope.isSubmitting = true;
                    $http.post('/manage/security/setManageScope', params)
                        .then(function (resp) {
                            $scope.isSubmitting = false;
                            var data = resp.data;
                            if (data != null) {
                                if (data.flag === 'success') {
                                    toastrService.success('操作成功');
                                    $uibModalInstance.close();
                                }
                                else {
                                    toastrService.error(data.message);
                                }
                            }
                        });
                }
            }])
        .controller('OpenSecuController', [
            '$scope',
            '$uibModalInstance',
            '$rootScope',
            '$http',
            'toastrService',
            function($scope, $uibModalInstance, $rootScope, $http, toastrService) {
                'ngInject';

                $scope.addParams = [
                    {
                        relName: '',
                        idCard: '',
                        telephone: '',
                        scope: {
                            manageOrgId: '',
                            orgInfo: null
                        }
                    }
                ];
                $scope.isSubmitting = false;

                //开通 、添加
                $scope.add = function(batch) {
                    $scope.addForm.$setSubmitted();
                    if (!batch && ($scope.addForm.$invalid || $scope.isSubmitting)) {
                        return;
                    }

                    var params = null;
                    if (!batch) {
                        params = $scope.addParams;
                    }
                    else {
                        params = $scope.allImportList.map(function (x) {
                            return {
                                idCard: x.idCard,
                                relName: x.relName,
                                telephone: x.telephone
                            };
                        });

                        $scope.allImportList = null;
                    }
                    var currentClientId = $rootScope.selectClient.clientId;

                    params = params.map(function(x) {
                        var param = {
                            idCard: x.idCard,
                            relName: x.relName,
                            telephone: x.telephone,
                            clientId: currentClientId,
                            scope: {
                                clientId: currentClientId
                            }
                        };
                        var scope = x.scope;
                        if (scope != null) {
                            param.scope.manageOrgId = scope.manageOrgId;
                            var orgInfo = scope.orgInfo;
                            if (orgInfo != null) {
                                param.scope.manageOrgName = orgInfo.name;
                                param.scope.manageOrgCode = orgInfo.code;
                                var extData = orgInfo.extData;
                                if (extData != null) {
                                    param.scope.ouName = extData.ouName;
                                }
                            }
                        }

                        return param;
                    });

                    var hasNotOrg = params.some(function(x) { return (!x.scope) || (!x.scope.manageOrgId) || (!x.scope.manageOrgName) || (!x.scope.manageOrgCode); });
                    if (hasNotOrg) {
                        toastrService.warning('请重新选择组织');
                        return;
                    }

                    $scope.isSubmitting = true;
                    $http.post('/manage/security/addSecurityUser', params)
                        .then(function (data) {
                            $scope.isSubmitting = false;
                            var data = data.data;
                            if (data.flag == 'success') {
                                toastrService.success('开通安全员成功');
                                $uibModalInstance.close(data);
                            }
                            else {
                                toastrService.error(data.message);
                            }
                        })
                };
            }
        ]);

})(angular);