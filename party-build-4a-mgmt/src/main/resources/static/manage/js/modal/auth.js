(function (angular) {
    /* deprecate ReauthorizeController 是其重构版本，去掉了对多 client 同时授权的支持*/
    angular.module('mgnt')
        .controller('AuthController', [
            '$scope',
            '$window',
            '$timeout',
            '$uibModal',
            'toastrService',
            '$http',
            '$uibModalInstance',
            'roleService',
            'authUsers',
            'topOrg',
            '_',
            'ORG_LEVELS',
            function ($scope, $window, $timeout, $uibModal, toastrService, $http, $uibModalInstance, roleService, authUsers, topOrg, _, ORG_LEVELS) {
                'ngInject';
                $scope.ORG_LEVELS = ORG_LEVELS;
                $scope.isRoleLoading = false;

                this.$onInit = function () {
                    $scope.topOrg = topOrg;

                    $scope.businessUserIds = authUsers.businessUserIds;
                    var temp = authUsers.clientList;
                    var client = temp[0];
                    $scope.origClientList = temp;

                    $scope.isRoleLoading = true;
                    roleService.query({
                        pageNo: 1,
                        pageSize: 10000,
                        clientId: client.clientId
                    }).then(function(resp) {
                        $scope.isRoleLoading = false;
                        var data = resp.data;
                        if (data != null) {
                            temp[0].roles = data.data;
                            $scope.userClients = handleClientData(temp);
                            $scope.clientList = handleClientData(temp);
                            $scope.selectUser($scope.choosedUsers[0]);
                        }
                    });

                    $scope.choosedUsers = authUsers.businessUsers;
                    $scope.selectUser($scope.choosedUsers[0]);
                }
                var businessUserId = '';
                $scope.display = function (client) {
                    $scope.clientList.forEach(function (x) { x.chk = false });
                    client.chk = true;
                }

                $scope.selectChildren = function (client) {
                    client.roles.forEach(function (item) {
                        item.chk = client.chk;
                    })
                }

                $scope.authParmas = [];
                $scope.orgId = '';
                $scope.orgInfo = {
                    manageOrgName: '',
                    manageOrgCode: ''
                };
                $scope.clientIds = [];
                $scope.childrenRoles = [];

                $scope.$watch('clientList', function (newval, oldval) {
                    if (newval == oldval) {
                        return;
                    }
                    $scope.clientList.filter(function (x) { return x.chk }).forEach(function (item) {
                        $scope.childrenRoles.push(item.roles);
                    })
                }, true);

                //change the rolelist when select a user
                $scope.selectUser = function (user) {
                    //storage the "active" state
                    $scope.selectedUserName = user.businessUserName;
                    $scope.selectedUserId = user.businessUserId;
                    mergeUserRoles(user);
                }

                //process checked roles when specified user was selected
                var mergeUserRoles = function (user) {
                    var tempClients = angular.copy($scope.origClientList);
                    var roleList = user.clientList[0].roles;
                    if (roleList.length > 0) {
                        var client = tempClients[0];
                        if (client.roles && client.roles.length > 0) {
                            client.roles.forEach(function (x) {
                                var roles = roleList.filter(function(role) {
                                    return role.roleId == x.roleId;
                                });
                                var selected = x.chk = roles.length > 0;
                                if (selected) {
                                    x.orgList = roles.map(function(x) {
                                        return {
                                            orgId: x.manageId
                                        };
                                    });
                                }
                                else {
                                    x.orgList = [];
                                }
                            });
                        }
                    }

                    $scope.userClients = handleClientData(tempClients);
                }

                //transform data to display in "table"
                var handleClientData = function (data) {
                    var temp = [];
                    data.forEach(function (x) {
                        if (x.roles) {
                            x.roles.forEach(function (item, idx) {
                                var obj = angular.merge({}, x, item);
                                obj.clientName = idx == 0 ? x.clientName : "";
                                obj.rowspan = idx == 0 ? x.roles.length.toString() : null;
                                obj.manageId = obj.manageId || '';
                                temp.push(obj);
                            });
                        }
                    });
                    return temp;
                }
                $scope.checkOrg = function (client) {
                    $scope.authForm.$invalid = client.chk && !client.manageId;
                    return client.chk && !client.manageId;
                }

                //授权请求
                $scope.auth = function () {
                    var choosedRoles = $scope.userClients.filter(function (x) { return x.chk });
                    if (choosedRoles.length == 0) {
                        toastrService.warning('请选择角色');
                        return;
                    }
                    $scope.authForm.$setSubmitted();
                    if ($scope.authForm.$invalid) {
                        return;
                    }
                    // var unsetOrgs = choosedRoles.filter(function(x){return !x.manageId});
                    // if(unsetOrgs.length>0) {
                    //     $scope.userClients.forEach(function(x,idx){
                    //         if(x.chk&&!x.manageId) {                                
                    //             $("#"+x.roleId+'nullorg').removeClass('hide');
                    //         }
                    //     });
                    //     return ;
                    // }

                    // 过滤出全部的应用
                    var clientList = _.uniq(choosedRoles, 'clientId').map(function (x) { return { clientId: x.clientId } });
                    clientList.forEach(function (x) {
                        x.roles = [];
                        choosedRoles.filter(function (item) { return item.clientId == x.clientId })
                            .forEach(function (item) {
                                item.orgList.forEach(function(org) {
                                    var param = {
                                        roleId: item.roleId,
                                        manageId: org.orgId
                                    };
                                    var orgInfo = org.orgInfo;
                                    if (orgInfo != null) {
                                        param.manageName = orgInfo.name;
                                        param.level = orgInfo.treeLevel;
                                        var extData = orgInfo.extData;
                                        if (extData != null) {
                                            param.manageCode = extData.code;
                                            param.ouName = extData.ouName;
                                        }
                                    }
                                    x.roles.push(param);
                                });
                            });
                    });

                    var hasNotOrg = clientList.some(function(x) {
                        return (!x.roles)
                            || x.roles.some(function(role) { return (!role.manageId) || (!role.manageName) || (!role.manageCode); });
                    });
                    if (hasNotOrg) {
                        toastrService.warning('请重新选择组织');
                        return;
                    }

                    var authParmas = [];
                    authParmas.push({
                        businessUserId: $scope.choosedUsers[0].businessUserId,
                        clients: clientList
                    })

                    // var authParmas = choosedRoles.map(function(x){
                    //     return x;
                    // });



                    // clientList.forEach(function (x) {
                    //     if (!x.roles) {
                    //         x.roles = [];
                    //     }
                    // });
                    // // clientList = clientList.filter(function (x) {
                    // //     return x.roles.filter(function (item) { return item.chk }).length > 0;
                    // // });
                    // clientList.forEach(function (x) {
                    //     x.roles = x.roles.filter(function (item) {
                    //         return item.chk
                    //     }).map(function (x) {
                    //         return x.roleId;
                    //     })
                    // });
                    // clientList = clientList.map(function (x) {
                    //     return {
                    //         clientId: x.clientId,
                    //         roles: x.roles
                    //     }
                    // });

                    // var authParmas = choosedRoles.map(function (x) {
                    //     return {
                    //         businessUserId: $scope.selectedUserId,
                    //         clients: clientList                           
                    //     }
                    // });

                    $http.post('/manage/business/grantBusinessUser', authParmas)
                        .then(function (data) {
                            var result = data.data;
                            if (result) {
                                var flag = result.flag;
                                if (flag === 'success') {
                                    toastrService.success('操作成功');
                                    //一个用户授权时，保存成功后关闭弹窗
                                    if ($scope.choosedUsers.length == 1) {
                                        $scope.$close();
                                    }
                                }
                                else if (flag === 'fail') {
                                    toastrService.error(result.message);
                                }
                            }
                        })
                }
            }])
})(angular);