(function(angular) {
    angular.module('mgnt')
        .controller('ClearClientController', function($scope, $q, $uibModalInstance, toastrService, alert, ClearClientService, FUNCTIONS_TYPE, type, client, selectedUsers) {
            'ngInject';

            var clearClientService = new ClearClientService('/manage/' + type + '/');

            $scope.type = null;
            $scope.users = [];
            $scope.client = client;
            $scope.isClearing = false;

            this.$onInit = function() {
                $scope.type = FUNCTIONS_TYPE[type];
                $scope.users = selectedUsers;
                $scope.users.forEach(function(x) { x._checked = true; });

                $scope.check();
            };

            $scope.canDoClear = function() {
                var hasChecking = $scope.users.some(function(x) { return x.isChecking; });
                return hasChecking === false && $scope.isClearing === false;
            };

            $scope.check = function() {
                var userIds = $scope.users.map(function(x) {
                    x.isChecking = true;
                    return x.id;
                });
                clearClientService.check({
                    ids: userIds,
                    clientId: client.clientId
                })
                .then(function(resp) {
                    var data = resp.data;
                    if (data != null) {
                        $scope.users.forEach(function(x) {
                            x.isChecking = false;
                            x._checked = x.canClear = data.indexOf(x.id) === -1;
                        });
                    }
                });
            };

            $scope.clear = function() {
                var selectedUsers = $scope.users.filter(function(x) { return x._checked; });
                if (selectedUsers.length === 0) {
                    toastrService.warning('您未选中任何用户');
                    return;
                }

                var hasDangers = selectedUsers.every(function(x) { return x.canClear; }) === false;
                var confirmPromise = $q.resolve();
                if (hasDangers) {
                    confirmPromise = alert({
                        content: '仅有一个角色的用户被撤销角色后会删除用户账号、VPN账号等所有信息，该操作不可恢复，请谨慎操作！'
                    });
                }
                confirmPromise
                    .then(function() {
                        $scope.isClearing = true;
                        return clearClientService.doClear({
                            ids: selectedUsers.map(function(x) { return x.id; }),
                            clientId: client.clientId
                        });
                    })
                    .then(function(resp) {
                        var data = resp.data;
                        if (data) {
                            toastrService.success('撤销用户角色成功');
                            $uibModalInstance.close(data);
                        }
                        else {
                            toastrService.error('撤销用户角色失败，请稍后重试');
                        }
                    });
            };
        });
})(angular);
