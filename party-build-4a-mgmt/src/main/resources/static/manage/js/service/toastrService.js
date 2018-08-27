'use strict';

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

(function (angular) {
    /**
     * @ngdoc service
     * @name toastrServiceConfigProvider
     *
     * @description
     * toastrService静默提示的配置。
     */
    angular.module('mgnt').provider('toastrServiceConfig', function () {
        var _this = this;

        /** 
         * @ngdoc property
         * @name toastrServiceConfigProvider#sientCodes
         *
         * @description
         * 保存静默提示的代码。
         */
        this.silentCodes = [];

        /**
         * @ngdoc method
         * @name toastrServiceConfigProvider#addSilent
         * @param {*} code 要屏蔽显示的代码
         *
         * @description
         * 在屏蔽列表中添加业务代码。
         */
        this.addSilent = function (code) {
            var silentCodes = _this.silentCodes;

            if (silentCodes.indexOf(code) === -1) {
                // 如果该code已存在于静默列表中，则不再添加
                _this.silentCodes.push(code);
            }
        };

        /**
         * $ngdoc method
         * @name toastrServiceConfigProvider#removeSilent
         * @param {*} code 移除屏蔽的代码
         *
         * @description
         * 从屏蔽列表中移除业务代码。
         */
        this.removeSilent = function (code) {
            var silentCodes = _this.silentCodes;

            _this.silentCodes = silentCodes.filter(function (silentCode) {
                return silentCode !== code;
            });
        };

        this.$get = function () {
            return _this;
        };
    })
    /**
     * @ngdoc service
     * @name toastrService
     * @requires $log
     * @requires toastr
     * @requires toastrServiceConfigProvider
     *
     * @description
     * toastr代理Service，屏蔽某些不提示给用户的返回。
     */
    .factory('toastrService', ['$log', 'toastr', 'toastrServiceConfig', function ($log, toastr, toastrServiceConfig) {
        'ngInject';

        return {
            /**
             * @ngdoc method
             * @name toastrService#error
             * @methodOf toastrService
             * @param {String|Object} error 提示信息或提示对象，传入提示对象时会根据业务代码屏蔽不需要提示的消息。
             *
             * @description
             * toastr.error代理方法。
             */
            error: function error(_error) {
                if (typeof _error === 'string') {
                    toastr.error(_error);
                } else if ((typeof _error === 'undefined' ? 'undefined' : _typeof(_error)) === 'object' && _error != null) {
                    var code = _error.code,
                        message = _error.message;

                    if (toastrServiceConfig.silentCodes.indexOf(code) > -1) {
                        $log.log('error with code: ' + code + ' has message: ' + message + '.');
                    }
                    if (toastrServiceConfig.silentCodes.indexOf(code) === -1) {
                        toastr.error(message);
                    }
                }
            },
            success: function success() {
                for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
                    args[_key] = arguments[_key];
                }

                toastr.success.apply(toastr, args);
            },
            warning: function warning() {
                for (var _len2 = arguments.length, args = Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
                    args[_key2] = arguments[_key2];
                }

                toastr.warning.apply(toastr, args);
            },
            info: function info() {
                for (var _len3 = arguments.length, args = Array(_len3), _key3 = 0; _key3 < _len3; _key3++) {
                    args[_key3] = arguments[_key3];
                }

                toastr.info.apply(toastr, args);
            }
        };
    }]);
})(angular);
