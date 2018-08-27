(function (angular) {
    var SelectAllController = function SelectAllController($scope, $parse) {
        'ngInject';

        this.$onInit = function () {
            var selectedItems = $scope.selectedItems;


            if (angular.isUndefined(selectedItems)) {
                $scope.selectedItems = [];
            }
        };

        $scope.$watch('list', function () {
            var list = $scope.list,
                selectedKey = $scope.selectedKey;

            if (list == null) {
                $scope.selectedItems = [];
                $scope.allSelected = true;
                return;
            }

            $scope.selectedItems = list.filter(function (item) {
                return $parse(selectedKey)(item);
            });
            $scope.allSelected = $scope.selectedItems.length === list.length;
        }, true);

        $scope.toggleAll = function () {
            var list = $scope.list,
                selectedKey = $scope.selectedKey,
                allSelected = $scope.allSelected;


            list.forEach(function (item) {
                return $parse(selectedKey + ' = ' + allSelected)(item);
            });
        };
    };

    angular.module('mgnt')
        .directive('selectAll', function () {
            return {
                controller: SelectAllController,
                transclude: true,
                scope: {
                    list: '=',
                    selectedItems: '=?',
                    selectedKey: '@'
                },
                template: '<input type="checkbox" ng-model="allSelected" ng-change="toggleAll()"><ng-transclude></ng-transclude>'
            };
        });
})(angular);
