(function(angular) {
    angular.module('mgnt')
        .factory('alertService', function ($uibModal) {
            'ngInject';

            /**
             * @type {String} description 要提示给用户的文案
             * @type {String} allow 确定按钮要显示文字，默认是确定
             */

            return function (_description) {
                var _allow = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : '确定';

                var alertService = $uibModal.open({
                    animation: true,
                    controller: ['$scope', '$uibModalInstance', 'description', 'allow', function controller($scope, $uibModalInstance, description, allow) {
                        $scope.description = description;
                        $scope.allow = allow;
                        $scope.ok = function () {
                            $uibModalInstance.close();
                        };
                    }],
                    templateUrl: 'alert-service.html',
                    size: 'md',
                    resolve: {
                        description: function description() {
                            return _description;
                        },
                        allow: function allow() {
                            return _allow;
                        }
                    }
                });

                return alertService.result.then(function () {
                    return true;
                }, function () {
                    return false;
                });
            };
        });
})(angular);
