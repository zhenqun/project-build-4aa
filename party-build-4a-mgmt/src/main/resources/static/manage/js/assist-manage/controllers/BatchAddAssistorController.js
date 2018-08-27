(function(angular) {
    angular.module('mgnt')
        .controller('BatchAddAssistorController',
            function ($scope, $q, $uibModalInstance, toastrService, _, assistManageService, batch, FLAGS) {
                'ngInject';
                var TELEPHONE_REG = /^1[3456789]\d{9}$/;

                $scope.isClientLoading = false;
                $scope.isFirstSubmit = false;
                $scope.isSubmitting = false;
                $scope.isSubmitted = false;
                $scope.isAllSuccess = false;
                $scope.totalActiviteCount = 0;
                $scope.activitedCount = 0;
                $scope.failureCount = 0;

                $scope.selectedAssistor = null;
                $scope.assistors = [];
                $scope.clientList = [];

                this.$onInit = function() {
                    var assistors = batch.assistors;
                    var ignoredCount = batch.ignoredCount;
                    if (ignoredCount > 0) {
                        toastrService.warning('已忽略' + ignoredCount + '行空数据');
                    }

                    $scope.loadClients()
                        .then(function(clients) {
                            if (clients != null) {
                                assistors.forEach(function(x) {
                                    x.clients = angular.copy(clients);
                                });
                                $scope.selectAssistor(assistors.find(function(x) { return x.isExist === false; }));
                            }
                        });

                    $scope.assistors = assistors;
                };

                $scope.loadClients = function() {
                    $scope.isClientLoading = true;

                    return assistManageService.queryClients()
                        .then(function(resp) {
                            $scope.isClientLoading = false;
                            var data = resp.data;
                            $scope.clientList = data || [];

                            return $scope.clientList;
                        })
                        .catch(function(error) {
                            $scope.isClientLoading = false;
                            toastrService.error(error);

                            return null;
                        });
                };

                $scope.selectAssistor = function(assistor) {
                    if (assistor.isExist) {
                        return;
                    }
                    $scope.selectedAssistor = assistor;
                };

                $scope.clientSelected = function(assistor, client) {
                    if (client._selected && !client.manageId) {
                        var clientList = assistor.clients;
                        var index = clientList.findIndex(function(x) { return x === client; });
                        var lastOrg = clientList.slice(0, index).reverse().find(function(x) { return x._selected && x.manageId; });
                        if (lastOrg != null) {
                            client.manageId = lastOrg.manageId;
                        }
                    }
                    else if (!client._selected) {
                        client.manageId = null;
                        client.manageName = null;
                    }
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
                    return user.idCard && user.relName && user.telephone;
                };

                $scope.isUserError = function(user) {
                    if ($scope.isFirstSubmit === false) {
                        return false;
                    }
                    if (user == null || user.isExist) {
                        return false;
                    }
                    user.$error = (user.clients.some(function(x) {
                        return x._selected && x.manageId;
                    })) === false;
                    return user.$error;
                };

                $scope.batchOpen = function(isRetry) {
                    if ($scope.isSubmitting) {
                        return;
                    }

                    $scope.isFirstSubmit = true;
                    var assistors = $scope.assistors
                        .filter(function(x) {
                            var flag = true;
                            if (isRetry) {
                                flag = x.$remote && !x.$remote.isSuccess;
                            }
                            return flag && (!x.isExist)
                                && $scope.isBaseInfoValid(x)
                                && $scope.isTelephoneValid(x.telephone);
                        });
                    var hasError = assistors.some(function(x) {
                        return $scope.isUserError(x);
                    });
                    if (hasError) {
                        toastrService.warning('所有辅助安全员都应该至少拥有一个应用，已为您标记没有应用的辅助安全员');
                        return;
                    }

                    var params = assistors.map(function(x) {
                        return {
                            fzuserId: x.fzuserId,
                            relName: x.relName,
                            idCard: x.idCard,
                            telephone: x.telephone,
                            remark: x.remark,
                            clients: x.clients
                                .filter(function(x) { return x._selected; })
                                .map(function(x) {
                                    var client = {
                                        clientId: x.clientId,
                                        clientName: x.clientName,
                                        manageId: x.manageId,
                                        createManageId: x.orgId
                                    };
                                    var orgInfo = x.manageName;
                                    if (orgInfo) {
                                        client.manageName = orgInfo.name;
                                        var extData = orgInfo.extData;
                                        if (extData) {
                                            client.manageCode = extData.code;
                                            client.ouName = extData.ouName;
                                        }
                                    }
                                    return client;
                                })
                        };
                    });

                    var firstOrgErrorUser = params.find(function(x) {
                        var errorClient = x.clients.find(function(client) {
                            return (!client.manageId) || (!client.manageName) || (!client.manageCode);
                        });
                        if (errorClient != null) {
                            x.errorClient = errorClient;
                        }
                        return errorClient != null;
                    });
                    if (firstOrgErrorUser != null) {
                        toastrService.warning('请为 ' + firstOrgErrorUser.relName + ' 分配的 ' + firstOrgErrorUser.errorClient.clientName + ' 重新选择党组织');
                        return;
                    }
                    assistors.forEach(function(x) { x.$remote = {}; });

                    $scope.isSubmitted = $scope.isSubmitting = true;
                    $scope.totalActiviteCount = params.length;
                    var userActiviting = params.map(function(x) {
                        var user = assistors.find(function(assistor) { return assistor.fzuserId === x.fzuserId; });
                        if (user != null) {
                            user.$remote.pending = true;
                        }
                        return assistManageService.add(_.omit(x, '$remote'))
                            .then(function(resp) {
                                if (user != null) {
                                    user.$remote.pending = false;
                                }
                                var result = resp.data;
                                if (result) {
                                    var flag = result.flag;
                                    var isSuccess = user.$remote.isSuccess = flag === FLAGS.Success;
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
                    $scope.$watch(
                        function() {
                            return userActiviting.map(function(x) {
                                return x.$$state
                            });
                        },
                        function(newVal) {
                            $scope.activitedCount = newVal.filter(function(x) {
                                return x.status !== 0;
                            }).length;
                        },
                        true
                    );
                    $q.all(userActiviting)
                        .then(function() {
                            $scope.isSubmitting = false;
                            var successCount = assistors.filter(function(x) { return x.$remote.isSuccess; }).length;
                            var isAllSuccess = $scope.isAllSuccess = successCount === assistors.length;
                            $scope.failureCount = assistors.length - successCount;

                            if (!isAllSuccess) {
                                $scope.assistors.sort(function(a, b) {
                                    var a$remote = a.$remote;
                                    var b$remote = b.$remote;
                                    if (a$remote && !b$remote) {
                                        return -1;
                                    }
                                    else if ((!a$remote) && b$remote) {
                                        return 1;
                                    }
                                    else if (a$remote.isSuccess === b$remote.isSuccess) {
                                        return 0;
                                    }
                                    else if (a$remote.isSuccess && !b$remote.isSuccess) {
                                        return 1;
                                    }
                                    return -1;
                                });
                            }
                        });
                };
            });
})(angular);
