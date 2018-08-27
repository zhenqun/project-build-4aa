'use strict';

(function (angular) {
	angular.module('mgnt')
		.factory('httpService', function ($http, $q) {
			'ngInject';

			return {
				post: function post(url, params) {
					var def = $q.defer();
					$http.post('' + url, params).then(function (_ref) {
						var data = _ref.data;
						var error = data.error;

						if (error) {
							def.reject(error);
						} else {
							def.resolve(data);
						}
					});
					return def.promise;
				}
			};
		});
})(angular);
