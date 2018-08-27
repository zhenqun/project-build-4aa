'use strict';

(function (angular) {
    angular.module('mgnt').provider('notificationService', function () {
        var _this2 = this;

        var socketUrl = 'ws://115.28.68.240:8499/';

        this.$get = ['$rootScope', '$q', function ($rootScope, $q) {
            'ngInject';

            var _this = _this2;

            _this2.connected = $q.defer();
            _this2.socket = io(socketUrl, { transports: ['websocket'] });

            _this2.socket.on('connect', function () {
                _this2.connected.resolve();
            });

            _this2.socket.on('msg.notify', function (data) {
                $rootScope.$broadcast('msg.notify', data);
            });

            _this2.socket.on('added.customer', function (data) {
                $rootScope.$broadcast('added.customer', data);
            });

            _this2.socket.on('top.customer', function (data) {
                $rootScope.$broadcast('top.customer', data);
            });

            return {
                connect: function connect() {
                    return _this.connected.promise;
                },
                changeWx: function changeWx(uins) {
                    _this.socket.emit('change.listener', uins);
                }
            };
        }];
    });
})(angular);