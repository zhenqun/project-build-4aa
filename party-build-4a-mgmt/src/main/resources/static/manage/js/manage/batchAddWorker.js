(function(angular) {
    angular.module('mgnt')
        .controller('BatchAddWorkerController',
            function ($scope, $http, $q, $uibModalInstance, toastrService, roleService, authUsers, topOrg, _, ORG_LEVELS) {
                'ngInject';
                var TELEPHONE_REG = /^1[3456789]\d{9}$/;

                $scope.ORG_LEVELS = ORG_LEVELS;
                $scope.isRoleLoading = false;
                $scope.isFirstSubmit = false;
                $scope.isSubmitted = false;
                $scope.isSubmitting = false;
                $scope.activitedCount = 0;
                $scope.totalActiviteCount = 0;
                $scope.ignoredCount = 0;

                this.$onInit = function () {
                    $scope.topOrg = topOrg;
                    var ignoredCount = authUsers.ignoredCount;
                    if (ignoredCount > 0) {
                        toastrService.warning('已忽略' + authUsers.ignoredCount + '行空数据');
                    }

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
                            $scope.choosedUsers.forEach(function(x) {
                                x.clients = handleClientData(temp);
                            });
                            $scope.userClients = handleClientData(temp);
                            $scope.clientList = handleClientData(temp);
                            $scope.selectUser($scope.choosedUsers.find(function(x) { return x.isExist === false; }));
                        }
                    });

                    $scope.choosedUsers = authUsers.businessUsers;
                    $scope.selectUser($scope.choosedUsers[0]);
                };

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
                    if (user.isExist) {
                        return;
                    }
                    $scope.selectedUser = user;
                };

                //transform data to display in "table"
                var handleClientData = function (data) {
                    return data.reduce(function (prevVal, x) {
                        if (!x.roles) {
                            return prevVal;
                        }

                        return prevVal.concat(
                            x.roles.map(function (item, idx) {
                                var obj = angular.merge({}, x, item);
                                obj.clientName = idx == 0 ? x.clientName : "";
                                obj.rowspan = idx == 0 ? x.roles.length.toString() : null;
                                obj.manageId = obj.manageId || '';
                                return obj;
                            })
                        );
                    }, []);
                }
                $scope.checkOrg = function (client) {
                    $scope.authForm.$invalid = client.chk && !client.manageId;
                    return client.chk && !client.manageId;
                };

                /**
                 * 验证手机号是否符合格式
                 * @param telephone
                 * @returns {boolean}
                 */
                $scope.isTelephoneValid = function(telephone) {
                    return TELEPHONE_REG.test(telephone);
                };

                /**
                 * 验证基本信息是否全部存在
                 * @param user
                 * @returns {*}
                 */
                $scope.isBaseInfoValid = function(user) {
                    if (user == null) {
                        return false;
                    }
                    return user.idCard && user.businessUserName && user.telephone;
                };

                $scope.isUserError = function(user) {
                    if ($scope.isFirstSubmit === false) {
                        return false;
                    }
                    if (user == null || user.isExist) {
                        return false;
                    }
                    user.$error = (user.clients.some(function(x) {
                        return x.chk && x.orgList.length > 0;
                    })) === false;
                    return user.$error;
                };

                //授权请求
                $scope.auth = function () {
                    if ($scope.authForm.$pending || $scope.isSubmitting) {
                        return;
                    }

                    $scope.isFirstSubmit = true;
                    var users = $scope.choosedUsers
                        .filter(function(x) {
                            return (!x.isExist)
                                && $scope.isBaseInfoValid(x)
                                && $scope.isTelephoneValid(x.telephone);
                        });
                    var hasError = users.some(function(x) {
                        return $scope.isUserError(x);
                    });
                    if (hasError) {
                        toastrService.warning('所有管理员都应该至少拥有一个角色，已为您标记没有角色的管理员');
                        return;
                    }

                    var params = users
                        .map(function(x) {
                            // 在每次请求前清空远程结果
                            x.$remote = {};
                            return {
                                businessUserId: x.businessUserId,
                                idCard: x.idCard,
                                relName: x.businessUserName,
                                telephone: x.telephone,
                                clientId: authUsers.clientList[0].clientId,
                                roles: x.clients
                                    .filter(function(x) {
                                        return x.chk;
                                    })
                                    .reduce(function(prevVal, clientRole) {
                                        return prevVal.concat(clientRole.orgList.map(function(org) {
                                            var role = {
                                                roleId: clientRole.roleId,
                                                roleName: clientRole.roleName,
                                                manageId: org.orgId,
                                                description: clientRole.roleDescription
                                            };
                                            var orgInfo = org.orgInfo;
                                            if (orgInfo != null) {
                                                role.manageName = orgInfo.name;
                                                if (orgInfo.extData != null) {
                                                    role.manageCode = orgInfo.extData.code;
                                                    role.ouName = orgInfo.extData.ouName;
                                                    role.level = orgInfo.extData.treeLevel;
                                                    role.type = orgInfo.extData.type;
                                                }
                                            }
                                            return role;
                                        }));
                                    }, [])
                            };
                        });
                    var firstOrgErrorUser = params.find(function(x) {
                        var errorRole = x.roles.find(function(role) {
                            return (!role.manageId) || (!role.manageName) || (!role.manageCode);
                        });
                        if (errorRole != null) {
                            x.errorRole = errorRole;
                        }
                        return errorRole != null;
                    });
                    if (firstOrgErrorUser != null) {
                        toastrService.warning('请为 ' + firstOrgErrorUser.relName + ' 分配的 ' + firstOrgErrorUser.errorRole.description + ' 重新选择党组织');
                        return;
                    }

                    $scope.isSubmitted = true;
                    $scope.isSubmitting = true;
                    var userActiviting = $scope.userActiviting = params.map(function(x) {
                        var user = users.find(function(user) {
                            return x.businessUserId === user.businessUserId;
                        });
                        if (user != null) {
                            user.$remote.pending = true;
                        }
                        return $http.post('/manage/business/addBusinessUser', [_.omit(x, '$remote')])
                            .then(function (data) {
                                if (user != null) {
                                    user.$remote.pending = false;
                                }
                                var result = data.data;
                                if (result) {
                                    var flag = result.flag;
                                    var isSuccess = user.$remote.isSuccess = flag === 'success';
                                    if (!isSuccess) {
                                        user.$remote.message = result.message;
                                    }
                                }
                            })
                            .catch(function(error) {
                                if (user != null) {
                                    user.$remote.pending = false;
                                }
                                user.$remote.isSuccess = false;
                                user.$remote.message = '服务器内部错误';
                            });
                    });
                    $scope.totalActiviteCount = userActiviting.length;
                    // 检查 Promises 状态，更新 activitedCount，更新界面，未实现
                    $scope.$watch(
                        function() {
                            return userActiviting
                        },
                        function(newVal) {
                            $scope.activitedCount = newVal.filter(function(x) {
                                return x.$$state.status !== 0;
                            }).length;
                        },
                        true
                    );
                    $q.all(userActiviting)
                        .then(function() {
                            $scope.isSubmitting = false;
                        });
                }
            });
})(angular);
