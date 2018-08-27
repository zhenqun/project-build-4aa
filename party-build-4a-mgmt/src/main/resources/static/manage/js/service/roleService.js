(function() {
    angular.module('mgnt')
        .service('roleService', function($http) {
            'ngInject';

            return {
                query: function(params) {
                    return $http.post('/manage/business/roleQuery', params);
                }
            };
        });
})(angular);
