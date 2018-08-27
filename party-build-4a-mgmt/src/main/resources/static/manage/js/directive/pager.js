'use strict';

var PagerController = ['$scope', function ($scope) {
    this.$onInit = function () {
        var previousText = $scope.previousText,
            nextText = $scope.nextText,
            pager = $scope.pager,
            pageSizeChanged = $scope.pageSizeChanged,
            currentPageChanged = $scope.currentPageChanged;


        if (angular.isUndefined(pager)) {
            $scope.pager = {
                no: 1,
                size: 10,
                totalItems: 0
            };
        }
        if (angular.isUndefined(previousText)) {
            $scope.previousText = '上一页';
        }
        if (angular.isUndefined(nextText)) {
            $scope.nextText = '下一页';
        }
        if (angular.isUndefined(pageSizeChanged)) {
            $scope.pageSizeChanged = function () {
                return undefined;
            };
        }
        $scope._pageSizeChanged = function () {
            $scope.pager.no = 1;
            $scope.pageSizeChanged();
        };
        if (angular.isUndefined(currentPageChanged)) {
            $scope.currentPageChanged = function () {
                return undefined;
            };
        }
    };
}];

angular.module('mgnt').directive('pager', function () {
    return {
        restrict: 'E',
        templateUrl: 'pager.html',
        controller: PagerController,
        scope: {
            previousText: '@?',
            nextText: '@?',
            pager: '=?',
            pageSizeChanged: '&?',
            currentPageChanged: '&?'
        }
    };
});