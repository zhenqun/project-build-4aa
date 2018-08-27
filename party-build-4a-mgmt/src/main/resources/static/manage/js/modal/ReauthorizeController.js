(function(angular) {
    angular.module('mgnt')
        .controller('ReauthorizeController', function($scope, $http, $uibModalInstance, toastrService, roleService, ORG_LEVELS, authUsers, topOrg) {
            'ngInject';

            $scope.isRoleLoading = false;
            $scope.isLoading = false;

            $scope.ORG_LEVELS = ORG_LEVELS;
            $scope.topOrg = null;
            $scope.client = null;
            $scope.users = [];
            $scope.selectedUser = null;

            this.$onInit = function() {
                $scope.topOrg = topOrg;

                var client = $scope.client = authUsers.clientList[0];
                var users = authUsers.businessUsers;

                $scope.client = client;

                $scope.isRoleLoading = true;
                roleService.query({
                    pageNo: 1,
                    pageSize: 10000,
                    clientId: client.clientId
                }).then(function(resp) {
                    $scope.isRoleLoading = false;
                    var data = resp.data;
                    if (data != null) {
                        // 应用下所有角色
                        client.roles = data.data;

                        users.forEach(function(user) {
                            var clientCopy = user.client = angular.copy(client);
                            // 用户在当前应用具有的角色
                            var userClient = user.clientList.find(function(x) { return x.clientId === clientCopy.clientId; });
                            if (userClient != null) {
                                // 将角色进行转换，每个角色对应多个管理范围
                                clientCopy.roles = clientCopy.roles.map(function(x) {
                                    var role = {
                                        roleId: x.roleId,
                                        roleDescription: x.roleDescription,
                                        detail: x.detail
                                    };
                                    // 查询用户角色中是否有该角色
                                    var userRoles = userClient.roles.filter(function(role) { return role.roleId == x.roleId; });
                                    role._selected = userRoles.length > 0;
                                    role.orgList = userRoles.map(function(x) {
                                        return {
                                            orgId: x.manageId,
                                            relId: x.relId
                                        };
                                    });
                                    return role;
                                });
                            }
                        });
                        $scope.users = users;
                        $scope.selectUser(users[0]);
                    }
                });
            };

            $scope.selectUser = function(user) {
                $scope.selectedUser = user;
            };

            $scope.save = function() {
                var user = $scope.selectedUser;
                if ($scope.isLoading) {
                    return;
                }
                var roles = user.client.roles.filter(function(cheRole){
                        return cheRole._selected;
                    })
                    .reduce(function(prevVal, role) {
                        if(role.orgList.length === 0){
                            toastrService.warning('请为选中的每个角色至少选择一个管理范围');
                            return;
                        }
                        return prevVal.concat(role.orgList.map(function(org) {
                            var roleDto = {
                                roleId: role.roleId,
                                manageId: org.orgId,
                                relId: org.relId
                            };
                            var orgInfo = org.orgInfo;
                            if (orgInfo != null) {
                                roleDto.manageName = orgInfo.name;
                                var extData = orgInfo.extData;
                                if (extData != null) {
                                    roleDto.manageCode = extData.code;
                                    roleDto.ouName = extData.ouName;
                                    roleDto.level = extData.treeLevel;
                                    roleDto.type = extData.type;
                                }
                            }
                            return roleDto;
                        }));
                }, []);
                var hasNotOrg = roles.some(function(x) {
                    return (!x.manageId) || (!x.manageName) || (!x.manageCode);
                });
                if (hasNotOrg) {
                    toastrService.warning('请重新选择组织');
                    return;
                }

                var params = [{
                    businessUserId: user.businessUserId,
                    clients: [{
                        clientId: user.client.clientId,
                        roles: roles
                    }]
                }];
                $http.post('/manage/business/grantBusinessUser', params)
                    .then(function (resp) {
                        var result = resp.data;
                        if (result) {
                            var flag = result.flag;
                            if (flag === 'success') {
                                toastrService.success('操作成功');
                                // 一个用户授权时，保存成功后关闭弹窗
                                if ($scope.users.length == 1) {
                                    $uibModalInstance.close();
                                }
                            }
                            else {
                                toastrService.error(result.message);
                            }
                        }
                    });
            };
        });
})(angular);
