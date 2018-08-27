(function (angular) {
    angular.module('mgnt')
        .controller('WorkController', [
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
            'alert',
            '_',
            '$rootScope',
            'topOrgService',
            'MODAL_DIALOG_CONFIGS',
            'ORG_LEVELS',
            function ($scope, $window, $timeout, $uibModal, $q, toastrService, Upload, $http, DEFAULT_PAGESIZE, exportUsersService, alert, _, $rootScope, topOrgService, MODAL_DIALOG_CONFIGS, ORG_LEVELS) {
                'ngInject';

                $scope.ORG_LEVELS = ORG_LEVELS;
                this.$onInit = function () {
                    // $scope.queryClients();
                    // $timeout(function () {
                    //     $scope.orgUrl = $scope.appList[0].classificationUrl;
                    // })

                }

                $rootScope.$on('clientsLoaded', function (e, param) {
                    $scope.appList = $rootScope.appList;
                    $scope.allAppRoles = $rootScope.allAppRoles;
                })

                $scope.file = null;
                $scope.contentLength = 0;

                $scope.totalItems = '';
                $scope.orgId = '';
                $scope.view = {
                    orgInfo: {}
                };
                $scope.page = {
                    pageNo: 1,
                    pageSize: DEFAULT_PAGESIZE
                };
                $scope.importPage = {
                    pageNo: 1,
                    pageSize: DEFAULT_PAGESIZE
                };
                $scope.isSubmitting = false;
                $scope.isLoading = false;
                $scope.totalItems = '';

                $scope.queryParams = {
                    manageOrgId: '',
                    state: '',
                    businessUserName: '',
                    clientId: '',
                    isActivation: ''
                };

                //首次加载显示顶层组织
                $scope.queryTopOrgClass = topOrgService.initTopOrg(function (data) {
                    $scope.orgLoading = false;
                    $scope.topOrg = data.data[0];
                    $scope.queryParams.manageOrgId = $scope.topOrg.orgId;
                    $scope.queryParams.level = $scope.topOrg.level;
                    $scope.displayOrg = $scope.topOrg.orgName;
                    $scope.queryUsers();
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
                            //首次加载显示顶层组织
                            $scope.orgLoading = true;
                            $scope.userList = [];
                            $scope.queryTopOrgClass($scope.queryParams.clientId);
                            $scope.otherAppList = $scope.appList.filter(function (x) {
                                return x.clientId != newval;
                            })
                        }
                    })
                });

                $('.modal-content').click(function () {
                    return false;
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

                $scope.queryUsers = function () {
                    $scope.isLoading = true;
                    var queryParams = angular.merge({}, $scope.queryParams, $scope.page);
                    var orgInfo = $scope.view.orgInfo;
                    if (orgInfo != null) {
                        var extData = orgInfo.extData;
                        if (extData != null) {
                            queryParams.level = extData.treeLevel;
                        }
                    }

                    $http.post('/manage/business/businessUserQuery', queryParams)
                        .then(function (data) {
                            var data = data.data;
                            $scope.isLoading = false;
                            if (data) {
                                $scope.userList = data.data || [];
                                $scope.totalItems = data.count || 0;
                                $scope.userList.forEach(function (x) {
                                    $scope.queryRoles(x);
                                })
                            }
                        })
                };


                //查询应用数据时禁用授权按钮
                $scope.noClients = function () {
                    return !$scope.appList || $scope.appList.length <= 0
                }

                //查询角色 展示在列表中
                $scope.queryRoles = function (user) {
                    user.rolesLoading = true;
                    $http.post('/manage/business/clientRoleQuery', { businessUserId: user.businessUserId,clientId:$rootScope.selectClient.clientId })
                        .then((function (data) {
                            user.rolesLoading = false;
                            user.clientList = [{ roles: [] }];
                            if (data.data) {
                                user.clientList = data.data;
                                // $scope.userList.find(function (x) { return x.businessUserId == id }).clients2 = data.data;
                            }
                        }))
                };

                //注销
                $scope.logout = function (userId) {
                    var businessUsers = $scope.userList.filter(function (x) { return x._selected })
                    if (!userId) {
                        if (businessUsers.length == 0) {
                            toastrService.warning('请选择管理员');
                            return;
                        }
                        var businessUserIds = businessUsers.map(function (x) { return x.businessUserId });
                    }
                    else {
                        var businessUserIds = [userId];
                    }
                    return alert({
                        content: '您确定要注销吗？'
                    }).then(function(data) {
                        $http.post('/manage/business/cancellationBusinessUser', { businessUserIds: businessUserIds })
                            .then(operation);

                    }, function(reject) {

                    });

                };

                //0 禁用状态 1启用状态
                $scope.state = function (state, userId) {
                    if (!userId) {
                        var businessUsers = $scope.userList.filter(function (x) { return x._selected })
                        if (businessUsers.length == 0) {
                            toastrService.warning('请选择管理员');
                            return;
                        }
                        var businessUserIds = businessUsers.map(function (x) { return x.businessUserId });
                    }
                    else {
                        var businessUserIds = [userId];
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
                        $http.post('/manage/business/modifyBusinessUserState', { businessUserIds: businessUserIds, state: state })
                            .then(operation);

                    });

                };

                $scope.closeModal = function (ele) {
                    $(ele).modal('hide');
                };

                // 导出
                $scope.exportUser = function () {
                    var businessUsers = $scope.userList.filter(function (x) { return x._selected })
                    if (businessUsers.length == 0) {
                        toastrService.warning('请选择管理员');
                        return;
                    }
                    var ids = businessUsers.map(function (x) { return x.businessUserId });
                    return exportUsersService.exportUser('/manage/business/exportAuthorizationCode', ids);
                };

                //打开分配业务管理员
                $scope.openDist = function () {
                    var businessUsers = $scope.userList.filter(function (x) { return x._selected })
                    if (businessUsers.length == 0) {
                        toastrService.warning('请选择管理员');
                        return;
                    }
                    var businessUserIds = businessUsers.map(function (x) { return x.businessUserId });
                    var modalInstance = $uibModal.open({
                        templateUrl: 'dist.html',
                        controller: 'DistController',
                        animation: true,
                        resolve: {
                            userIds: function () {
                                return businessUserIds
                            }
                        }
                    });
                    modalInstance.result.then(function () {
                        $scope.queryUsers();
                    }, function () { });
                };

                $scope.batchOpen = function() {
                    $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                        templateUrl: 'batch-open-modal.html',
                        controller: 'BusinessUploadController',
                        resolve: {
                            selectedClient: function() {
                                return $rootScope.selectClient;
                            }
                        }
                    })).result.then(function(result) {
                        var businessUsers = result.users;
                        var ignoredCount = result.rawUsers.length - businessUsers.length;
                        if (businessUsers != null) {
                            businessUsers.forEach(function(x) {
                                if (x.telephone) {
                                    x.telephone = x.telephone.toString();
                                }
                            });
                        }
                        return $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                            templateUrl: 'batch-add-worker.html',
                            controller: 'BatchAddWorkerController',
                            size: 'lg',
                            windowClass: 'modal-batch-add-worker',
                            resolve: {
                                topOrg: function() {
                                    return $scope.topOrg;
                                },
                                authUsers: function () {
                                    return {
                                        businessUsers: businessUsers,
                                        ignoredCount: ignoredCount,
                                        //授权时只对外层选中的应用下的角色进行授权
                                        clientList: angular.copy([$rootScope.selectClient])
                                    }
                                }
                            }
                        })).result;
                    }).then(function() {
                        $scope.queryUsers();
                    });
                };

                //打开授权
                $scope.openAuthorty = function (user) {
                    var businessUserIds = null;
                    var businessUsers = null;
                    if (!user) {
                        businessUsers = $scope.userList.filter(function (x) { return x._selected })
                        if (businessUsers.length == 0) {
                            toastrService.warning('请选择管理员');
                            return;
                        }
                        businessUserIds = businessUsers.map(function (x) { return x.businessUserId });
                    }
                    else {
                        var userId = user.businessUserId;
                        businessUserIds = [userId];
                        businessUsers = [user]
                    }

                    var modalInstance = $uibModal.open({
                        templateUrl: 'reauthorize.html',
                        controller: 'ReauthorizeController',
                        animation: true,
                        size: 'lg',
                        resolve: {
                            topOrg: function() {
                                return $scope.topOrg;
                            },
                            authUsers: function () {
                                return {
                                    businessUserIds: businessUserIds,
                                    businessUsers: businessUsers,
                                    //授权时只对外层选中的应用下的角色进行授权
                                    clientList: [$scope.allAppRoles.find(function (x) { return x.clientId == $rootScope.selectClient.clientId })]
                                }
                            }
                        }
                    });
                    modalInstance.result.then(function () {
                        $scope.queryUsers();
                    }, function () { });
                };

                $scope.openWorker = function() {
                    $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                        templateUrl: 'open-worker-modal.html',
                        controller: 'OpenWorkerController',
                        size: 'lg',
                        resolve: {
                            clientList: function() {
                                return [$scope.allAppRoles.find(function (x) { return x.clientId == $rootScope.selectClient.clientId })];
                            },
                            topOrg: function() {
                                return $scope.topOrg;
                            }
                        }
                    })).result.then(function() {
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
                        toastrService.warning('请选择管理员');
                        return;
                    }

                    $uibModal.open(angular.extend({}, MODAL_DIALOG_CONFIGS, {
                        templateUrl: 'clear-client-modal.html',
                        controller: 'ClearClientController',
                        resolve: {
                            type: function() { return 'business'; },
                            client: function() { return $rootScope.selectClient; },
                            selectedUsers: function() {
                                return users.map(function(x) {
                                    var user = angular.extend({}, x, {
                                        id: x.businessUserId,
                                        name: x.businessUserName
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
                        toastrService.warning('请选择管理员');
                        return;
                    }
                    alert({
                        content: '确定要将 ' + user.businessUserName + ' 的 VPN 密码重置为身份证号后六位吗？该操作不可恢复，请谨慎操作。'
                    })
                        .then(function() {
                            return $http.post('/manage/business/resetVpnPassword/' + user.businessUserId);
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
            '$timeout',
            '$uibModal',
            'toastrService',
            '$http',
            '$uibModalInstance',
            'userIds',
            function ($scope, $window, $timeout, $uibModal, toastrService, $http, $uibModalInstance, userIds) {
                'ngInject';
                this.$onInit = function () {

                }
                $scope.orgId = '';
                $scope.orgInfo = {
                    manageOrgName: '',
                    manageOrgCode: ''
                };
                $scope.manageOrgId = '';
                $scope.businessUserIds = userIds;
                $scope.dist = function () {
                    if (!$scope.orgId) {
                        toastrService.warning('请选择组织机构');
                        return;
                    }
                    $http.post('/manage/business/setManageScope', { manageOrgId: $scope.orgId, businessUserIds: $scope.businessUserIds, manageOrgName: $scope.orgInfo.name, manageOrgCode: $scope.orgInfo.code })
                        .then(function (data) {
                            if (data) {
                                toastrService.success('操作成功');
                                $scope.$close();
                            }
                        })
                }
            }])
        .controller('OpenWorkerController', [
            '$scope',
            '$uibModalInstance',
            '$rootScope',
            '$http',
            'toastrService',
            'roleService',
            'ORG_LEVELS',
            'clientList',
            'topOrg',
            function($scope, $uibModalInstance, $rootScope, $http, toastrService, roleService, ORG_LEVELS, clientList, topOrg) {
                'ngInject';

                $scope.ORG_LEVELS = ORG_LEVELS;
                $scope.isSubmitting = false;
                $scope.currentClient = $rootScope.selectClient;
                $scope.isRoleLoading = false;
                $scope.addParams = [
                    {
                        relName: '',
                        idCard: '',
                        telephone: '',
                        roles: []
                    }
                ];

                var handleClientData = function (data) {
                    var temp = [];

                    if (data.roles) {
                        temp = data.roles.map(function (item, idx) {
                            var obj = angular.merge({}, data, item, {
                                clientName: '',
                                rowspan: null
                            });
                            if (idx === 0) {
                                obj.clientName = data.clientName;
                                obj.rowspan = data.roles.length.toString();
                            }
                            obj.manageId = obj.manageId || '';
                            return obj;
                        });
                    }

                    return temp;
                };

                this.$onInit = function() {
                    var client = $scope.currentClient;

                    $scope.isRoleLoading = true;
                    roleService.query({
                        pageNo: 1,
                        pageSize: 10000,
                        clientId: client.clientId
                    }).then(function(resp) {
                        $scope.isRoleLoading = false;
                        var data = resp.data;
                        if (data != null) {
                            clientList[0].roles = data.data;
                            $scope.clientList = handleClientData(clientList[0]);
                        }
                    });
                    $scope.topOrg = topOrg;
                };

                //开通 、添加
                $scope.add = function (batch) {
                    var selectedRoles = $scope.clientList.filter(function(x) { return x.chk; });
                    $scope.addForm.$setSubmitted();
                    if (!batch && ($scope.addForm.$invalid || $scope.isSubmitting)) {
                        return;
                    };
                    if (selectedRoles.length === 0) {
                        toastrService.warning('请选择至少一个角色');
                        return;
                    }
                    var selectManage= selectedRoles.every(function (x) {
                        return (x.orgList.length >0);
                    });
                    if(!selectManage){
                        toastrService.warning('请为选中的每个角色至少选择一个管理范围');
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
                            }
                        });
                        // params.clientIds = $scope.addParams.clientIds;
                        $scope.allImportList = null;
                    }
                    var clientId = $rootScope.selectClient.clientId;

                    params.forEach(function(x) {
                        x.clientId = clientId;
                        x.roles = selectedRoles.reduce(function(prevVal, x) {
                            return prevVal.concat(x.orgList.map(function(org) {
                                var role = {
                                    roleId: x.roleId,
                                    roleName: x.roleName,
                                    manageId: org.orgId,
                                    description: x.description
                                };
                                var orgInfo = org.orgInfo;
                                if (orgInfo != null) {
                                    role.manageName = orgInfo.name;
                                    var extData = orgInfo.extData;
                                    if (extData != null) {
                                        role.manageCode = extData.code;
                                        role.ouName = extData.ouName;
                                        role.level = extData.treeLevel;
                                        role.type = extData.type;
                                    }
                                }
                                return role;
                            }));
                        }, []);
                    });

                    var hasNotOrg = params.some(function(x) {
                        return (!x.roles)
                            || x.roles.some(function(role) { return (!role.manageId) || (!role.manageName) || (!role.manageCode); });
                    });
                    if (hasNotOrg) {
                        toastrService.warning('请重新选择组织');
                        return;
                    }

                    $scope.isSubmitting = true;
                    $http.post('/manage/business/addBusinessUser', params)
                        .then(function (data) {
                            var data = data.data;
                            $scope.isSubmitting = false;
                            if (data.flag == 'success') {
                                toastrService.success('开通管理员成功');
                                $uibModalInstance.close(data);
                            }
                            else {
                                toastrService.error(data.message);
                            }

                        });
                };
            }
        ]);

})(angular);