(function (angular) {
    /**
     * @ngdoc
     * @name party-build-develop.directive:ktDict
     *
     * @description
     * 字典选择组件
     * 依赖 ktDict, dictManager -> cacheManager
     *
     * @restrict A
     * @element input
     * @param {string?} displayValue 选中字典值的显示值。
     * @param {string?} [scope=all|user] 字典的范围，仅限 code 为 ZZ 时使用，标识查询的当前登录用户的组织机构或者全部，默认 'user'。
     * @param {boolean=} [allowTop=false] 是否允许选择顶级节点，仅限 code 为 ZZ 时使用，默认 false。
     * @param {boolean=} [enableSearch=true] 是否允许搜索字典，code 不为 ZZ 时生效。
     */
    angular.module('mgnt')
        .directive('ktDict', function ($rootScope) {
            'ngInject';

            return {
                restrict: 'A',
                require: ['?ngModel', '?^^ngModel'],
                scope: {
                    displayValue: '=?',
                    allowTop: '=?',
                    enableSearch: '=?',
                    orgCanSearch: '=?',
                    scope: '@?',
                    clientid: '@',
                    treeLevel: '=?'
                },
                template: '<input type="text">',
                link: function link(scope, element, attrs, ctrl) {
                    element.prop('readonly', true);

                    scope.clientCanSearch = false;

                    if (angular.isUndefined(scope.scope)) {
                        scope.scope = 'user';
                    }

                    if (ctrl[1]) {
                        ctrl[1].$parsers.push(function (viewValue) {
                            return viewValue;
                        });

                        ctrl[1].$render = function () {
                            scope.clientId = ctrl[1].$viewValue;
                        };

                        scope.$watch('clientId', function (newval, oldval) {
                            // if (angular.isUndefined(scope.client)) {
                            //     scope.client = '';
                            // }
                            // 动态传入查询url
                            $(element).data().ktDict.clientId = scope.clientId;
                        });
                    }

                    // 设置 scope 参数只在 ZZ 时有效
                    if (scope.scope !== 'user' || attrs.ktDict !== 'ZZ') {
                        scope.scope = null;
                    }
                    /*
                        enableSearch 是一个开关，确定是否自动计算
                        实际上 enableSearch = clientCanSearch + orgCanSearch
                     */
                    scope.$enableSearch = scope.enableSearch;
                    var enableSearch = scope.enableSearch;
                    if (angular.isUndefined(enableSearch)) {
                        enableSearch = false;
                        if ($rootScope.selectClient) {
                            enableSearch = $rootScope.selectClient.ifCanSearch === '1' && ENABLE_SEARCH;
                        }
                    }
                    // enableSearch 参数只在非 ZZ 时有效
                    if (enableSearch === 'auto') {
                        if ($rootScope.selectClient) {
                            scope.clientCanSearch = $rootScope.selectClient.ifCanSearch === '1';
                        }
                        enableSearch = scope.clientCanSearch && scope.orgCanSearch;
                    }
                    scope.enableSearch = enableSearch;

                    if (angular.isUndefined(scope.url)) {
                        scope.url = 'common/getOrganizations';
                    }
                    $(element).ktDict({
                        val: ctrl[0].$viewValue,
                        input: $(element),
                        // root: scope.scope,
                        allowTop: scope.allowTop,
                        enableSearch: enableSearch,
                        url: 'common/getOrganizations',
                        clientId: scope.clientid,
                        treeLevel: scope.treeLevel
                    });

                    ctrl[0].$render = function () {
                        $(element).ktDict('setValue', ctrl[0].$viewValue, attrs.ktDict);
                    };
                    ctrl[0].$parsers.push(function (viewValue) {
                        return viewValue;
                    });
                    $rootScope.$watch('selectClient', function(newVal, oldVal) {
                        if (newVal === oldVal) {
                            return;
                        }
                        scope.clientCanSearch = newVal.ifCanSearch === '1';
                        scope.$enableSearch = scope.clientCanSearch && scope.orgCanSearch;
                        $(element).ktDict('updateView', scope.$enableSearch);
                    });
                    element.on('change.ktDict', function (event, data) {
                        ctrl[0].$setViewValue(data);
                        scope.$emit('ktDict.ValueChanged', data, attrs);
                    }).on('displayValueChange.ktDict', function (event, data) {
                        if ($rootScope.$$phase) {
                            scope.displayValue = data;
                            scope.$emit('ktDict.DisplayValueChanged', data, attrs);
                        } else {
                            scope.$apply(function (scope) {
                                scope.displayValue = data;
                                scope.$emit('ktDict.DisplayValueChanged', data, attrs);
                            });
                        }
                    });
                }
            };
        });
})(angular);
