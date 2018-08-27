/**
@fileOverview

@toc

*/

'use strict';

angular.module('oitozero.ngSweetAlert', [])
.provider('sweetAlert', function() {
	this.setDefaults = window.swal.setDefaults;

	this.$get = function() {
		return this;
	};
})
.factory('SweetAlert', [ '$rootScope', '$q', function ( $rootScope, $q ) {

	var swal = window.swal;

	//public methods
	var self = {

		_swal: swal,

		swal: function ( arg1, arg2, arg3 ) {
			var def = $q.defer();
			$rootScope.$evalAsync(function(){
				if( typeof(arg2) === 'function' ) {
					swal( arg1, function(isConfirm){
						def.resolve(isConfirm);
					}, arg3 );
				} else if ( typeof(arg3) !== 'string' ) {
					if (typeof arg1 === 'string') {
						swal({
							title: arg1,
							text: arg2
						}, function(isConfirm){
							def.resolve(isConfirm);
						});
					}
					else {
						swal(arg1, function(isConfirm){
							def.resolve(isConfirm);
						});
					}
				}
			});
			return def.promise;
		},
		success: function(title, message) {
			$rootScope.$evalAsync(function(){
				swal( title, message, 'success' );
			});
		},
		error: function(title, message) {
			$rootScope.$evalAsync(function(){
				swal( title, message, 'error' );
			});
		},
		warning: function(title, message) {
			$rootScope.$evalAsync(function(){
				swal( title, message, 'warning' );
			});
		},
		info: function(title, message) {
			$rootScope.$evalAsync(function(){
				swal( title, message, 'info' );
			});
		}
	};

	return self;
}]);
