'use strict';

(function (angular) {
    angular.module('mgnt').provider('fileDownloadService', function () {
        var _this2 = this;

        /**
         * @ngdoc service
         * @name seo.service:fileDownloadService
         * @description 前端实现文件下载的Service。
         *
         * @requires $document
         */
        this.$get = ['$document', function ($document) {
            'ngInject';

            var _this = _this2;

            return {
                /**
                 * @ngdoc method
                 * @name orm.service:fileDownloadService#open
                 * @methodOf orm.service:fileDownloadService
                 * @description 前端构造一个超连接并模拟用户点击，实现在新窗口下载的功能。
                 *
                 * @param {String} url 目标Url
                 * @param {String} [fileName=url] 下载时显示的文件名，默认与Url一致。
                 */
                open: function open(url) {
                    var fileName = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : url;

                    var document = $document[0];
                    var download = document.createElement('a');
                    download.download = fileName;
                    download.href = url;
                    var e = document.createEvent('MouseEvent');
                    e.initEvent('click', true, true);
                    download.dispatchEvent(e);
                }
            };
        }];
    });
})(angular);