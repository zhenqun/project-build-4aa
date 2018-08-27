(function (angular) {
    angular.module('mgnt').controller('MultipleOrgSelectController', function ($scope) {
        'ngInject';

        // 组件内使用的 orgList 和传入的不同，传入的数组中只能拿到有效的

        var _this = this;

        $scope.orgList = [];
        $scope.isSingle = false;

        this.$onInit = function () {
            var orgList = _this.orgList;
            var isSingle = _this.isSingle;
            if (angular.isUndefined(isSingle)) {
                isSingle = false;
            }
            $scope.isSingle = isSingle;
            if (!Array.isArray(orgList)) {
                _this.orgList = [];
            }
            $scope.orgList = angular.copy(_this.orgList);
            if ($scope.orgList.length === 0) {
                $scope.addRow();
            }
        };

        $scope.$watch('orgList', function (newVal, oldVal) {
            if (newVal === oldVal) {
                return;
            }
            _this.orgList = newVal.filter(function (x) {
                return x.orgId;
            });
        }, true);

        $scope.addRow = function () {
            $scope.orgList.push({
                orgId: '',
                orgInfo: null
            });
        };

        $scope.removeRow = function (row) {
            var orgList = $scope.orgList;

            $scope.orgList = orgList.filter(function (x) {
                return x !== row;
            });
        };
    })
    /**
     * @ngdoc
     * @name assist.directive:multipleOrgSelect
     *
     * @description
     * 多个管理范围选择组件
     *
     * @element input
     * @param {Object} client 应用系统对象。
     * @param {Object} role 角色对象。
     * @param {Array.<Object>} [orgList=[]] 多个管理范围存储的数组，使用方只能获得有效的数据，无效数据不会被外界获取。
     */
    .component('multipleOrgSelect', {
        controller: 'MultipleOrgSelectController',
        templateUrl: 'multiple-org-select.html',
        bindings: {
            client: '=',
            role: '=',
            orgList: '=',
            isSingle: '=?',
            enableSearch: '=?',
            orgCanSearch: '=?',
            treeLevel: '=?'
        }
    });
})(angular);