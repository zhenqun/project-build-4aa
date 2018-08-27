(function (angular) {
    angular.module('mgnt')
        .service('topOrgService', function ($http) {
            'ngInject';

            //首次加载显示顶层组织
            this.initTopOrg = (function () {
                return function (fn) {
                    return function (clientId) {
                        $http.get('common/getOrganizations?clientId='+clientId+'&orgId=')
                            .then(fn)
                    };
                }
            }());
        });
}(angular));
