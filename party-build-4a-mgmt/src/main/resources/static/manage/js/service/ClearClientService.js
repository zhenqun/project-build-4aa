(function(angular) {
    function ClearClientService(baseUrl) {
        this.baseUrl = baseUrl;
    }
    ClearClientService.prototype.check = function(params) {
        return ClearClientService.$http.post(this.baseUrl + 'checkCancelUser', params);
    };
    ClearClientService.prototype.doClear = function(params) {
        return ClearClientService.$http.post(this.baseUrl + 'cancelUserClient', params);
    };

    angular.module('mgnt')
        .service('ClearClientService', function($http) {
            'ngInject';

            ClearClientService.$http = $http;
            return ClearClientService;
        });
})(angular);
