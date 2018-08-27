(function (angular) {
    angular.module('mgnt')
        .controller('AuthConfigController', [
            '$scope',
            '$window',
            '$timeout',
            '$uibModal',
            'toastrService',
            '$http',
            '$rootScope',
            'resultHandlerService',
            'topOrgService',
            'DEFAULT_PAGESIZE', 'alert',
            function ($scope, $window, $timeout, $uibModal, toastrService, $http,
                $rootScope, resultHandlerService, topOrgService, DEFAULT_PAGESIZE, alert) {
                'ngInject';
                this.$onInit = function () {

                }

                //query data when app changes 
                $rootScope.$watch('selectClient.clientId ', function (newval, oldval) {
                    if (newval == oldval) {
                        return;
                    }
                    $timeout(function () {
                        if (!$rootScope.clients.isLoading) {
                            $scope.ifCanCreateRole = $rootScope.selectClient.ifCanCreateRole;
                            $scope.queryParams.clientId = $scope.addParams.clientId = $rootScope.selectClient.clientId;
                            $scope.query();
                            $scope.queryAllPermissions($rootScope.selectClient.clientId);
                        }
                    })
                });

                //page
                $scope.page = {
                    pageNo: 1,
                    pageSize: DEFAULT_PAGESIZE
                };
                $scope.totalItems = '';
                //page end

                $scope.queryParams = {
                    clientId: '',
                    roleDescription: ''
                };

                //可添加角色（‘0’可添加   ‘1’不可添加）
                $scope.ifCanCreateRole = '';

                $scope.addParams = {
                    clientId: '',
                    roleDescription: '',
                    deatil: '',
                    roleName: '',
                    roleId: '',
                    permissionIds: []
                };

                // 权限
                $scope.permissions = {};

                // 授权加载状态
                $scope.permissionLoading = {};

                $scope.resetAddParams = function () {
                    $('#modal-4').modal('hide');
                    $scope.isSubmitting = false;
                    $scope.firstOpen = true;
                    $scope.addParams = {
                        clientId: $rootScope.selectClient.clientId,
                        roleDescription: '',
                        roleName: '',
                        roleId: '',
                        permissionIds: []
                    }
                };

                //操作处理函数
                var resultHandler = resultHandlerService.resultHandler;

                //查询当前应用下的角色
                $scope.query = function () {
                    $scope.isLoading = true;
                    var params = angular.merge({}, $scope.page, $scope.queryParams);
                    $http.post('/manage/business/roleQuery', params)
                        .then(function (result) {
                            $scope.isLoading = false;
                            if (result) {
                                var data = result.data;
                                $scope.roleList = data.data;
                                // //为每一个角色加入授权详情
                                // $scope.roleList.forEach(function (x) {
                                //     $scope.setPermissionsInRole(x);
                                // })

                                $scope.totalItems = data.count;
                            }
                        })
                };

                // //查询指定角色授权详情
                // $scope.setPermissionsInRole = function (role) {
                //     role.isLoading = true;
                //     $http.post('/manage/business/rolePermissionQuery', role.roleId)
                //         .then(function (result) {
                //             role.isLoading = false;
                //             if (result.data) {
                //                 role.permissions = result.data;
                //             }
                //         })
                // }

                //查询当前应用下的所有权限列表 
                $scope.queryAllPermissions = function (clientId) {
                    // 判断是否有缓存
                    if ($scope.permissions[clientId]) {
                        return
                    }
                    $http.post('/manage/business/permissionQuery', { clientId: clientId })
                        .then(function (result) {
                            if (result.data) {
                                $scope.permissions[clientId] = result.data;
                            }
                        })
                }

                //删除角色
                $scope.del = function (role) {
                    return alert({
                        content: '您确定要删除吗？'
                    }).then(function (data) {
                        $scope.deling = true;
                        $http.get('/manage/business/roleDel/' + role.roleId)
                            .then(resultHandler(function () {
                                $scope.deling = false;
                                $scope.query()
                            }));
                    });

                };

                //打开编辑新增角色
                $scope.openEdit = function (role) {
                    // 没有选择角色则传入新增参数
                    if (typeof role === 'undefined') {
                        role = $scope.addParams;
                    }
                    role.clientId = $rootScope.selectClient.clientId;
                    var modalInstance = $uibModal.open({
                        templateUrl: 'role.html',
                        controller: 'RoleEditController',
                        animation: true,
                        resolve: {
                            role: function () {
                                return {
                                    role: angular.copy(role),
                                    permissions: angular.copy($scope.permissions[$rootScope.selectClient.clientId])
                                }
                            }
                        }
                    });
                    if (!role) {
                        $scope.resetAddParams();
                    }
                    modalInstance.result.then(function () {
                        $scope.query();
                    }, function () { });
                };
            }])
        .controller('RoleEditController', [
            '$scope',
            '$window',
            '$timeout',
            '$uibModal',
            'toastrService',
            '$http',
            '$uibModalInstance',
            'role',
            '_',
            function ($scope, $window, $timeout, $uibModal, toastrService, $http, $uibModalInstance, role, _) {
                this.$onInit = function () {
                    $scope.role = role.role;
                    $scope.role.roleName = $scope.role.roleName.substring(5);
                    $scope.permissions = role.permissions;
                    $scope.selectItem = $scope.permissions[0] || [];
                    $scope.setPermissionsInRole($scope.role, $scope.permissions);
                }

                //查询指定角色授权详情,并与全部权限合并展示
                $scope.setPermissionsInRole = function (role, permissions) {
                    role.isLoading = true;
                    if (role.roleId != '') {
                        $http.post('/manage/business/rolePermissionQuery', {
                            roleId: role.roleId
                        }).then(function (result) {
                            role.isLoading = false;
                            if (result.data) {
                                var authedPermissions = result.data.map(function (x) {
                                    return angular.merge({}, x, { _selected: true })
                                });
                                role.permissions = permissions;
                                // role.permissions = angular.merge({}, permissions, authedPermissions)
                                role.permissions.forEach(function (item) {
                                    item.permissions.forEach(function (x) {
                                        if (authedPermissions.find(function (item) { return item.permissionId == x.permissionId })) {
                                            x._selected = true;
                                        }
                                    })
                                })
                            }
                            else {
                                role.permissions = permissions;
                            }
                        })
                    }
                    else {
                        role.permissions = $scope.permissions;
                    }
                };
                $scope.setItem = function (item) {
                    $scope.selectItem = item;
                }
                //编辑 or 添加
                $scope.edit = function () {
                    $scope.addForm.$setSubmitted();
                    if ($scope.addForm.$invalid) {
                        return;
                    }
                    if ($scope.selectItem.permissions.filter(function (x) { return x._selected }).length == 0) {
                        toastrService.warning("请选择权限");
                        return
                    }
                    $scope.isSubmitting = true;
                    var params = angular.copy($scope.role);
                    var permissionIds = [];
                    $scope.permissions.forEach(function (item) {
                        permissionIds = permissionIds.concat(item.permissions.filter(function (x) { return x._selected }).map(function (x) { return x.permissionId }));
                    });

                    params.permissionIds = permissionIds;
                    params = _.pick(params, 'roleName', 'roleId', 'roleDescription', 'permissionIds', 'clientId', 'detail');
                    params.roleName = 'ROLE_' + params.roleName;
                    $http.post('/manage/business/roleManage', params)
                        .then(function (result) {
                            var data = result.data;
                            $scope.isSubmitting = false;
                            if (data.flag === 'success') {
                                toastrService.success('操作成功')
                                $scope.$close();
                            }
                            else {
                                toastrService.error(data.message);
                            }
                        })
                };



            }])
})(angular);