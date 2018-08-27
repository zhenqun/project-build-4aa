'use strict';
(function (angular) {
    angular.module('payment')
        .component('roleUrl', {
            templateUrl: 'roleUrl.html',
            controller: function ($scope) {                            
                var getUrlParam = function (name) {
                    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
                    var r = window.location.search.substr(1).match(reg);
                    if (r != null) {
                        return unescape(r[2]);
                    }
                    else {
                        return null;
                    }
                };
                $scope.role = getUrlParam('role'); 
                this.role = $scope.role||'0';
            },
            bindings: {
                role: '='
            }
        })
}(angular))