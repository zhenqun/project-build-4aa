'use strict';

angular.module('mgnt').factory('mobileClientService', ['$http', '$q', function ($http, $q) {
    'ngInject';

    return {
        userList: function userList(params) {
            var def = $q.defer();
            $http.post('/mobile/queryUserWxRel', params).then(function (data) {
                if (data != null) {
                    def.resolve(data);
                } else {
                    def.reject(error);
                }
            });

            return def.promise;
        },
        groupList: function groupList(params) {
            var def = $q.defer();
            $http.post('/mobile/group/getwatagroupsByUser', params).then(function (data) {
                if (data != null) {
                    def.resolve(data);
                } else {
                    def.reject(error);
                }
            });

            return def.promise;
        }
    };
}]);