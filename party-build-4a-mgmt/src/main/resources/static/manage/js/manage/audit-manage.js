(function (angular) {
    angular.module('mgnt')
        .controller('AuditController', [
            '$scope',
            '$window',
            '$timeout',
            '$uibModal',
            '$q',
            'toastrService',
            'Upload',
            '$http',
            'DEFAULT_PAGESIZE',
            'exportUsersService',
            'SweetAlert',
            'alert',
            '$rootScope',
            'topOrgService',
            'ORG_LEVELS',
            'MODAL_DIALOG_CONFIGS',
            function ($scope, $window, $timeout, $uibModal, $q, toastrService, Upload,
                $http, DEFAULT_PAGESIZE, exportUsersService, SweetAlert, alert, $rootScope, topOrgService, ORG_LEVELS, MODAL_DIALOG_CONFIGS) {
                'ngInject';

                $scope.ORG_LEVELS = ORG_LEVELS;
                $scope.contentLength = 0;
                $scope.firstOpen = true;
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
                $scope.isSubmitting = false;
                $scope.isLoading = false;
                $scope.totalItems = '';
                $scope.queryParams = {
                    manageOrgId: '',
                    state: '',
                    auditorName: '',
                    isActivation: ''
                };
                $scope.addParams = [
                    {
                        relName: '',
                        idCard: '',
                        telephone: ''
                    }
                ];
                $scope.userList = [];
                $scope.resetAddParams = function () {
                    $scope.isSubmitting = false;
                    $('#modal-4').modal('hide');
                    $scope.firstOpen = true;
                    $scope.addParams = [
                        {
                            relName: '',
                            idCard: '',
                            telephone: ''
                        }
                    ]
                };
                $('.modal-content').click(function () {
                    return false;
                })


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
                    toastrService.success('操作成功');
                    $('#modal-4').modal('hide');
                    $('#modal-7').modal('hide');
                }


                //开通 、添加
                $scope.add = function (batch) {
                    $scope.firstOpen = false;
                    $scope.addForm.$setSubmitted();
                    if (!batch && ($scope.addForm.$invalid || $scope.isSubmitting)) {
                        return;
                    };
                    $scope.isSubmitting = true;
                    if (!batch) {
                        var params = $scope.addParams;
                    }
                    else {
                        var params = $scope.allImportList.map(function (x) {
                            return {
                                idCard: x.idCard,
                                relName: x.relName,
                                telephone: x.telephone
                            }
                        });

                        $scope.allImportList = null;
                    }
                    params.forEach(function (x) {
                        x.clientId = $rootScope.selectClient.clientId;
                    });

                    // addSuccessHandler(params, { flag: 'success', message: '123123|许宁' });
                    // return;



                    $http.post('/manage/auditor/addAuditorUser', params)
                        .then(function (data) {
                            $scope.resetAddParams();
                            var data = data.data;
                            $scope.isSubmitting = false;
                            if (data.flag == 'success') {
                                addSuccessHandler(params, data);
                                $scope.queryUsers();
                            }
                            else {
                                toastrService.error(data.message);
                            }
                        })
                };

                $scope.queryUsers = function () {
                    $scope.isLoading = true;
                    var queryParams = angular.merge({}, $scope.queryParams, $scope.page)
                    queryParams.manageOrgName = $scope.orgInfo.name;
                    queryParams.manageOrgCode = $scope.orgInfo.code;
                    $http.post('/manage/auditor/auditorUserQuery', queryParams)
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
                    var auditorUsers = $scope.userList.filter(function (x) { return x._selected })
                    if (!userId) {
                        if (auditorUsers.length == 0) {
                            toastrService.warning('请选择安全员');
                            return;
                        }
                        var auditorUserIds = auditorUsers.map(function (x) { return x.auditorUserId });
                    }
                    else {
                        var auditorUserIds = [userId];
                    }

                    return alert({
                        content: '您确定要注销吗？'
                    }).then(function(data) {
                        $http.post('/manage/auditor/cancellationAuditorUser', { auditorUserIds: auditorUserIds })
                            .then(operation);
                    });
                };

                //0 禁用状态 1启用状态
                $scope.state = function (state, userId) {
                    if (!userId) {
                        var auditorUsers = $scope.userList.filter(function (x) { return x._selected })
                        if (auditorUsers.length == 0) {
                            toastrService.warning('请选择安全员');
                            return;
                        }
                        var auditorUserIds = auditorUsers.map(function (x) { return x.auditorUserId });
                    }
                    else {
                        var auditorUserIds = [userId];
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
                        $http.post('/manage/auditor/modifyAuditorUserState', { auditorUserIds: auditorUserIds, state: state })
                            .then(operation);
                    });

                };

                //批量导入                
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
                    var auditorUsers = $scope.userList.filter(function (x) { return x._selected })
                    if (auditorUsers.length == 0) {
                        toastrService.warning('请选择审计员');
                        return;
                    }
                    var ids = auditorUsers.map(function (x) { return x.auditorId });
                    return exportUsersService.exportUser('/manage/business/exportAuthorizationCode', ids);
                };

                //首次加载显示顶层组织
                $scope.queryTopOrgClass = topOrgService.initTopOrg(function (data) {
                    $scope.orgLoading = false;
                    $scope.topOrg = data.data[0];
                    $scope.queryParams.manageOrgId = $scope.topOrg.orgId;
                    $scope.displayOrg = $scope.topOrg.orgName;
                    $scope.queryUsers();
                });

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
                        toastrService.warning('请选择审计员');
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
                                        id: x.auditorId,
                                        name: x.auditorName
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
                        toastrService.warning('请选择审计员');
                        return;
                    }
                    alert({
                        content: '确定要将 ' + user.auditorName + ' 的 VPN 密码重置为身份证号后六位吗？该操作不可恢复，请谨慎操作。'
                    })
                        .then(function() {
                            return $http.post('/manage/auditor/resetVpnPassword/' + user.auditorId);
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
            }
        ])
        .controller('DistController', [
            '$scope',
            '$window',
            '$timeout',
            '$uibModal',
            'toastrService',
            '$http',
            '$uibModalInstance',
            'userIds',
            function ($scope, $window, $timeout, $uibModal, toastrService, $http, $uibModalInstance, userIds) {
                'ngInject';
                this.$onInit = function () {
                    $scope.auditorUsers = userIds.auditorUsers;
                    $scope.selectClientId = userIds.selectClientId;
                }
                $scope.orgId = '';

                $scope.manageOrgId = '';
                $scope.orgInfo = {
                    name: '',
                    code: ''
                };

                $scope.dist = function () {

                    var params = $scope.auditorUsers.map(function (x) {
                        return {
                            manageOrgId: x.manageId,
                            manageOrgName: x.orgInfo.name,
                            manageOrgCode: x.orgInfo.code,
                            auditorUserId: x.auditorId,
                            clientId: $scope.selectClientId
                        };
                    })
                    $http.post('/manage/auditor/setManageScope', params)
                        .then(function (data) {
                            if (data) {
                                toastrService.success('操作成功');
                                $scope.$close();
                            }
                        })
                }
            }])

})(angular);