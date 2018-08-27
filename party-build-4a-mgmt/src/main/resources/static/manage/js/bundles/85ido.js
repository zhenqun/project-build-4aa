/*!
 * 85ido 前端库。
 *
 * @version 0.1.6
 * @author 85Ued: [ Colin Niu, FunTian ]
 */
(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory(require("jQuery"), require("_"), require("Backbone"), require("Handlebars"));
	else if(typeof define === 'function' && define.amd)
		define(["jQuery", "_", "Backbone", "Handlebars"], factory);
	else {
		var a = typeof exports === 'object' ? factory(require("jQuery"), require("_"), require("Backbone"), require("Handlebars")) : factory(root["jQuery"], root["_"], root["Backbone"], root["Handlebars"]);
		for(var i in a) (typeof exports === 'object' ? exports : root)[i] = a[i];
	}
})(this, function(__WEBPACK_EXTERNAL_MODULE_4__, __WEBPACK_EXTERNAL_MODULE_5__, __WEBPACK_EXTERNAL_MODULE_6__, __WEBPACK_EXTERNAL_MODULE_17__) {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};

/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {

/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;

/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};

/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);

/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;

/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}


/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;

/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;

/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";

/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	module.exports = __webpack_require__(1);


/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, '__esModule', {
		value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _views = __webpack_require__(2);

	var _views2 = _interopRequireDefault(_views);

	var _constant = __webpack_require__(10);

	var _constant2 = _interopRequireDefault(_constant);

	var _util = __webpack_require__(9);

	var _util2 = _interopRequireDefault(_util);

	var _messageManager = __webpack_require__(21);

	var _messageManager2 = _interopRequireDefault(_messageManager);

	var _plugins = __webpack_require__(22);

	var _underscore = __webpack_require__(5);

	var _underscore2 = _interopRequireDefault(_underscore);

	window._85ido = ("0.1.6");

	exports['default'] = _underscore2['default'].extend({
		Constant: _constant2['default'],
		Util: _util2['default'],
		MessageManager: _messageManager2['default']
	}, _views2['default']);
	module.exports = exports['default'];

/***/ },
/* 2 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, '__esModule', {
		value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _baseView = __webpack_require__(3);

	var _baseView2 = _interopRequireDefault(_baseView);

	exports['default'] = {
		BaseView: _baseView2['default']
	};
	module.exports = exports['default'];

/***/ },
/* 3 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * 基于 Backbone 优化的 BaseView。
	 *
	 * @module views/baseView
	 * @author Colin Niu
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
	    value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _jquery = __webpack_require__(4);

	var _jquery2 = _interopRequireDefault(_jquery);

	var _underscore = __webpack_require__(5);

	var _underscore2 = _interopRequireDefault(_underscore);

	var _backbone = __webpack_require__(6);

	var _backbone2 = _interopRequireDefault(_backbone);

	var _modules = __webpack_require__(7);

	var _modules2 = _interopRequireDefault(_modules);

	var _util = __webpack_require__(9);

	var _util2 = _interopRequireDefault(_util);

	var proto = function proto(options) {
	    this.initializors = [];
	    this.model = {};
	    this.cid = _underscore2['default'].uniqueId('view');

	    _underscore2['default'].extend(this, _underscore2['default'].omit(options, ['initializors']));

	    this._useModules(this.use);
	    this._useModules(options);

	    this.setElement((0, _jquery2['default'])(this.element));

	    for (var _len = arguments.length, args = Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
	        args[_key - 1] = arguments[_key];
	    }

	    for (var i = 0; i < this.initializors.length; i++) {
	        var _initializors$i;

	        (_initializors$i = this.initializors[i]).call.apply(_initializors$i, [this].concat(args));
	    }
	};

	/**
	 * @alias BaseView
	 */
	_underscore2['default'].extend(proto.prototype, {
	    /**
	     * 用来设置该View的控制范围，在此element内的事件可以通过该View处理。
	     *
	     * @type {string|selector|DOM}
	     */
	    element: undefined,

	    /**
	     * 将后续用到的一些常用功能(称之为module)注册到这里，配合use一起使用
	     */
	    modules: {},

	    /**
	     * 要使用的module，初始化时需要用到, 当其为数组时，其元素可以是string，也可以是Module定义。当其值为*时，表示加载所有已定义的module。
	     *
	     * @type {array|string}
	     */
	    use: '*',

	    /**
	     * 在 initialize 时被调用，按照 use 的值装在 module。
	     *
	     * @private
	     * @param {string|array|object} modules 要装载的 module。
	     */
	    _useModules: function _useModules(modules) {
	        if (!modules) {
	            return;
	        }

	        if (modules === '*') {
	            modules = _underscore2['default'].values(BaseView.modules);
	        }

	        if (!_underscore2['default'].isArray(modules)) {
	            modules = [modules];
	        }

	        for (var i = 0; i < modules.length; i++) {
	            var mod = modules[i];
	            if (_underscore2['default'].isString(mod) && BaseView.modules[mod]) {
	                this._useModule(BaseView.modules[mod]);
	            } else if (_underscore2['default'].isObject(mod)) {
	                this._useModule(mod);
	            }
	        }
	    },

	    /**
	     * useModules 的实现方法。
	     *
	     * @private
	     * @param {Object} mod 要装载的 module。
	     */
	    _useModule: function _useModule(mod) {
	        _underscore2['default'].extend(this, _underscore2['default'].omit(mod, ['events', 'initialize', 'model']));
	        _underscore2['default'].extend(this.events, mod.events);
	        _underscore2['default'].extend(this.model, {}, mod.model);
	        if (_underscore2['default'].isFunction(mod.initialize)) {
	            this.initializors.push(mod.initialize);
	        }
	    }
	});

	var BaseView = _backbone2['default'].View.extend(proto.prototype);
	BaseView.modules = {};

	/**
	 * 注册 module，module 应该是一个对象，包含了要注册的一个或多个 module 的名称和对象。
	 *
	 * @params {Object} module 包含了一个或多个 module 的名称和对象。
	 * @return {BaseView} this
	 */
	BaseView.register = function (module) {
	    _underscore2['default'].extend(this.modules, module);
	    return this;
	};

	BaseView.register(_modules2['default']);

	exports['default'] = BaseView;
	module.exports = exports['default'];

/***/ },
/* 4 */
/***/ function(module, exports) {

	module.exports = __WEBPACK_EXTERNAL_MODULE_4__;

/***/ },
/* 5 */
/***/ function(module, exports) {

	module.exports = __WEBPACK_EXTERNAL_MODULE_5__;

/***/ },
/* 6 */
/***/ function(module, exports) {

	module.exports = __WEBPACK_EXTERNAL_MODULE_6__;

/***/ },
/* 7 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, '__esModule', {
				value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _dateTimePicker = __webpack_require__(8);

	var _dateTimePicker2 = _interopRequireDefault(_dateTimePicker);

	var _dialogFormBase = __webpack_require__(11);

	var _dialogFormBase2 = _interopRequireDefault(_dialogFormBase);

	var _loading = __webpack_require__(12);

	var _loading2 = _interopRequireDefault(_loading);

	var _preload = __webpack_require__(13);

	var _preload2 = _interopRequireDefault(_preload);

	var _queryView = __webpack_require__(18);

	var _queryView2 = _interopRequireDefault(_queryView);

	var _tableRow = __webpack_require__(19);

	var _tableRow2 = _interopRequireDefault(_tableRow);

	var _templateAutoRender = __webpack_require__(20);

	var _templateAutoRender2 = _interopRequireDefault(_templateAutoRender);

	exports['default'] = {
				dateTimePicker: _dateTimePicker2['default'],
				dialogFormBase: _dialogFormBase2['default'],
				loading: _loading2['default'],
				preload: _preload2['default'],
				queryView: _queryView2['default'],
				tableRow: _tableRow2['default'],
				templateAutoRender: _templateAutoRender2['default']
	};
	module.exports = exports['default'];

/***/ },
/* 8 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * bootstrap-datetimepicker 插件实例化。
	 *
	 * @module modules/dateTimePicker
	 * @author FunTian
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
	    value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _jquery = __webpack_require__(4);

	var _jquery2 = _interopRequireDefault(_jquery);

	var _util = __webpack_require__(9);

	var _util2 = _interopRequireDefault(_util);

	// 初始化datetimepicker最小可选时间
	var startTime = new Date(-28800001);
	// 初始化仅时间选择时，时间在当天选择。
	var timeBegin = new Date();
	timeBegin.setHours(0);
	timeBegin.setMinutes(0);
	timeBegin.setSeconds(0);
	timeBegin.setMilliseconds(0);
	var timeEnd = new Date();
	timeEnd.setHours(23);
	timeEnd.setMinutes(59);
	timeEnd.setSeconds(59);
	timeEnd.setMilliseconds(999);

	exports['default'] = {
	    /**
	     * 初始化 datetimepicker，可以指定每种预定义类型的配置。
	     *
	     * @param {?{dateOptions: object, timeOptions: object, dateTimeOptions: object}} options datetimepicker 的配置，参见 {@link http://www.bootcss.com/p/bootstrap-datetimepicker/#options|bootstrap-datetimepicker}。
	     * @param {Object} options.dateOptions 日期 datetimepicker 的配置。
	     * @param {Object} options.timeOptions 时间 datetimepicker 的配置。
	     * @param {Object} options.dateTimeOptions 日期时间 datetimepicker 的配置。
	     *
	     */
	    initDateTimePicker: function initDateTimePicker(options) {
	        if (typeof _jquery2['default'].fn.datetimepicker === 'undefined') {
	            _util2['default'].warn('Initialize datetimepicker failed, because of jQuery have not datetimepicker method.');
	            return;
	        }

	        var dateOptions = {
	            format: 'yyyy-mm-dd',
	            minView: 'month',
	            startDate: startTime
	        };
	        var timeOptions = {
	            format: 'hh:ii:00',
	            startView: 'day',
	            minView: 'hour',
	            startDate: timeBegin,
	            endDate: timeEnd
	        };
	        var dateTimeOptions = {
	            format: 'yyyy-mm-dd hh:ii:00',
	            startDate: startTime
	        };

	        if (options != null) {
	            if (options.date != null) {
	                _.extend(dateOptions, options.date);
	            }
	            if (options.time != null) {
	                _.extend(timeOptions, options.time);
	            }
	            if (options.dateTime != null) {
	                _.extend(dateTimeOptions, options.dateTime);
	            }
	        }

	        this.$('.date-picker').datetimepicker(dateOptions);

	        this.$('.time-picker').datetimepicker(timeOptions);

	        this.$('.date-time-picker').datetimepicker(dateTimeOptions);
	    }
	};
	module.exports = exports['default'];

/***/ },
/* 9 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * Utilities。
	 *
	 * @module util
	 * @author FunTian
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
		value: true
	});

	var _constant = __webpack_require__(10);

	exports['default'] = {
		/**
	  * console.warn 的封装，只会在 Debug 模式下打印。
	  *
	  * @param {...any} args 调用 console.warn 的参数。
	  */
		warn: function warn() {
			if (_constant.DEBUG) {
				for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
					args[_key] = arguments[_key];
				}

				console.warn.apply(console, args);
			}
		},
		/**
	  * console.error 的封装，只会在 Debug 模式下打印。
	  *
	  * @param {...any} args 调用 console.error 的参数。
	  */
		error: function error() {
			if (_constant.DEBUG) {
				for (var _len2 = arguments.length, args = Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
					args[_key2] = arguments[_key2];
				}

				console.error.apply(console, args);
			}
		}
	};
	module.exports = exports['default'];

/***/ },
/* 10 */
/***/ function(module, exports) {

	/**
	 * 库中使用的常量。
	 *
	 * @module constant
	 * @author FunTian
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
		value: true
	});
	exports['default'] = {
		/**
	  * 是否调试模式。
	  *
	  * @type {boolean}
	  */
		DEBUG: false,
		/**
	  * 默认配置项。
	  *
	  * @property {string} displayField preload 模块使用，默认显示字段。
	  * @property {string} valueField preload 模块使用，默认取值字段。
	  */
		defaults: {
			displayField: 'label',
			valueField: 'value'
		}
	};
	module.exports = exports['default'];

/***/ },
/* 11 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * 弹出框中使用数据绑定的 module。
	 *
	 * @module modules/dialogFormBase
	 * @author FunTian
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
		value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _underscore = __webpack_require__(5);

	var _underscore2 = _interopRequireDefault(_underscore);

	exports['default'] = {
		/**
	  * 初始化数据绑定，将当前 View 的 DOM 与 model 绑定。
	  *
	  * @param {Object} options 配置对象。
	  * @param {Backbone.View} options.parent 当前 View 的父 View。
	  * @param {Object} options.dialogOptions options dialog 的配置，参见 {@link http://api.jqueryui.com/dialog/|jQuery UI Dialog}。
	  * @fires "dialog-close.base" 在弹出框关闭后在父级 View 触发。
	  */
		init: function init(options) {
			var _this = this;
			_underscore2['default'].extend(this, _underscore2['default'].pick(options, 'parent', 'dialogOptions'));

			// 保持model中的key与表单中input的name相同，否则会报出错误
			this.$el.binddata(this.model, {
				onlyGetOrSet: 'set'
			});

			var opts = {
				resizable: false,
				close: function close() {
					_this.parent.trigger('dialog-close.base');
				}
			};
			this.$el.dialog(_underscore2['default'].extend({}, opts, this.dialogOptions));
		}
	};
	module.exports = exports['default'];

/***/ },
/* 12 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * 加载状态提示。
	 *
	 * @module modules/loading
	 * @author FunTian
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
	    value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _jquery = __webpack_require__(4);

	var _jquery2 = _interopRequireDefault(_jquery);

	var _backbone = __webpack_require__(6);

	var _backbone2 = _interopRequireDefault(_backbone);

	exports['default'] = {
	    /**
	     * [data-role="loading"] 选择的结果。
	     *
	     * @private
	     */
	    _$loading: null,

	    /**
	     * ajaxStart 回调函数。
	     *
	     * @private
	     */
	    _onAjaxStart: function _onAjaxStart() {
	        var $loading = this._$loading;

	        if ($loading != null && $loading.length > 0) {
	            $loading.removeClass('hide');
	        }
	    },

	    /**
	     * ajaxStop 回调函数。
	     *
	     * @private
	     */
	    _onAjaxStop: function _onAjaxStop() {
	        var $loading = this._$loading;

	        if ($loading != null && $loading.length > 0) {
	            $loading.addClass('hide');
	        }
	    },

	    /**
	     * 监听全局的 ajaxSend,ajaxComplete 事件，显示加载状态。
	     */
	    initialize: function initialize() {
	        this._onAjaxStart = this._onAjaxStart.bind(this);
	        this._onAjaxStop = this._onAjaxStop.bind(this);

	        this._$loading = this.$('[data-role="loading"]');
	        (0, _jquery2['default'])(document).on('ajaxStart', this._onAjaxStart).on('ajaxStop', this._onAjaxStop);
	    },
	    /**
	     * 停止监听全局 ajaxSend 和 ajaxComplete 不再显示加载状态。
	     *
	     * @override @{link http://backbonejs.org/#View-remove|Backbone.View#remove}
	     */
	    remove: function remove() {
	        _backbone2['default'].View.prototype.remove.apply(this);

	        (0, _jquery2['default'])(document).off('ajaxStart', this._onAjaxStart).off('ajaxStop', this._onAjaxStop);
	        this._$loading = null;
	    }
	};
	module.exports = exports['default'];

/***/ },
/* 13 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * 将数据预加载并单向绑定到 select。
	 *
	 * @module modules/preload
	 * @author FunTian
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
	    value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _jquery = __webpack_require__(4);

	var _jquery2 = _interopRequireDefault(_jquery);

	var _underscore = __webpack_require__(5);

	var _underscore2 = _interopRequireDefault(_underscore);

	var _constant = __webpack_require__(10);

	var _constant2 = _interopRequireDefault(_constant);

	var _util = __webpack_require__(9);

	var _util2 = _interopRequireDefault(_util);

	var _variables = __webpack_require__(14);

	exports['default'] = {
	    /**
	     *
	     * 预加载数据并在完成时渲染。<br/>
	     *
	     * 会解析所有 data-preload 元素的配置属性进行渲染。<br/>
	     * 有效配置属性：
	     * - *`data-preload` 加载数据的 Url。
	     * - `data-display-field`(default=constant.defaults.displayField) 显示字段的 key。
	     * - `data-value-field`(default=constant.defaults.valueField) 取值字段的 key。
	     * - `data-result-key`(default="result") 从响应的 JSON 中取值的 path，不指定值是为整个响应对象。
	     * - `data-default-value` select 的默认值，渲染完成后将选中这个值。
	     *
	     * @example
	     * <select data-preload="types/" data-display-field="label" data-value-field="id" data-result-key="data"></select>
	     *
	     * json:
	     * {
	     *     "data": [] // data will render on select
	     * }
	     */
	    preload: function preload() {
	        var _this = this;

	        var autoId = 1;
	        var preloadResult = {};
	        var promises = _variables.slice.call(this.$('[data-preload]')).map(function (item) {
	            var $item = _this.$(item);
	            var preload = $item.attr('data-preload');
	            var displayField = $item.attr('data-display-field') || _constant2['default'].defaults.displayField;
	            var valueField = $item.attr('data-value-field') || _constant2['default'].defaults.valueField;
	            var resultKey = $item.attr('data-result-key');
	            var defaultValue = $item.attr('data-default-value');
	            /*
	             * 基于jQuery取出的属性，
	             * 元素上未定义该属性时值为undefined，
	             * 元素上仅定义了key或者key=""时值为""
	             */
	            if (typeof resultKey === 'undefined') {
	                resultKey = 'result';
	            }
	            var uri = preload;
	            /*
	             * 执行到这里一定定义了data-preload属性，
	             * 因为是通过[data-preload]选择到所有需要预加载的select，
	             * 故无需考虑uri == null的情况
	             */
	            var key = $item.attr('name') || 'preload-select-' + autoId++;
	            return Promise.resolve(_jquery2['default'].postJSON(uri)).then(function (data) {
	                var result = undefined;
	                if (resultKey.length === 0) {
	                    result = data;
	                } else {
	                    result = data[resultKey];
	                }
	                _this.renderSelect($item, result, displayField, valueField, defaultValue);

	                if (preloadResult.hasOwnProperty(key)) {
	                    _util2['default'].warn('Preload success but can\'t get result, because of element\'s name has already defined: ' + key + '.');
	                }
	                preloadResult[key] = result;
	            }, function () {
	                for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
	                    args[_key] = arguments[_key];
	                }

	                preloadResult[key] = args;
	            });
	        });
	        return new Promise(function (resolve, reject) {
	            Promise.all(promises).then(function () {
	                _util2['default'].warn(preloadResult);
	                resolve(preloadResult);
	            }, function () {
	                _util2['default'].warn(preloadResult);
	                reject(preloadResult);
	            });
	        });
	    },

	    /**
	     * 将对象渲染到 select。
	     *
	     * @private
	     * @param {"jQuery DOM"} $select select 元素。
	     * @param {array|array-like} options 数据，应至少包含 displayField 和 valueField 指定的字段。
	     * @param {string} displayField 显示字段的 key。
	     * @param {string} valueField 取值字段的 key。
	     * @param {Object} [selected] 选中的值，该值应该为 options 中某项的 valueField 对应的值。
	     * @fires "select-rendered.base" 渲染完成后触发。
	     */
	    renderSelect: function renderSelect($select, options, displayField, valueField, selected) {
	        if (options == null) {
	            _util2['default'].warn('Can not render ' + options + ' to options');
	            return;
	        }
	        var _results = [];
	        for (var i = 0; i < options.length; i++) {
	            var option = options[i];
	            _results.push((0, _variables.optionTemplate)({
	                value: option[valueField],
	                text: option[displayField]
	            }));
	        }
	        $select.find('option[value!=""]').remove().end().append(_results.join(''));

	        if (typeof selected !== 'undefined') {
	            $select.val(selected);
	        }
	        this.trigger('select-rendered.base', options);
	    }
	};
	module.exports = exports['default'];

/***/ },
/* 14 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, '__esModule', {
		value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _slice = __webpack_require__(15);

	var _slice2 = _interopRequireDefault(_slice);

	var _optionTemplate = __webpack_require__(16);

	var _optionTemplate2 = _interopRequireDefault(_optionTemplate);

	exports['default'] = {
		slice: _slice2['default'],
		optionTemplate: _optionTemplate2['default']
	};
	module.exports = exports['default'];

/***/ },
/* 15 */
/***/ function(module, exports) {

	"use strict";

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	exports["default"] = [].slice;
	module.exports = exports["default"];

/***/ },
/* 16 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, '__esModule', {
	  value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _handlebars = __webpack_require__(17);

	var _handlebars2 = _interopRequireDefault(_handlebars);

	exports['default'] = _handlebars2['default'].compile('<option value="{{value}}"{{#if selected}} selected{{/if}}>{{text}}</option>');
	module.exports = exports['default'];

/***/ },
/* 17 */
/***/ function(module, exports) {

	module.exports = __WEBPACK_EXTERNAL_MODULE_17__;

/***/ },
/* 18 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * 查询和列表。
	 *
	 * @listens th[order-field] click
	 * @listens [cmd=query] click
	 * @listens [cmd=page] click
	 * @listens [cmd=selectAll] click
	 * @listens [cmd=pageSize] change
	 * @listens .query-form input[name] keypress
	 *
	 * @module modules/queryView
	 * @author Colin Niu
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
	    value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _jquery = __webpack_require__(4);

	var _jquery2 = _interopRequireDefault(_jquery);

	var _underscore = __webpack_require__(5);

	var _underscore2 = _interopRequireDefault(_underscore);

	var _handlebars = __webpack_require__(17);

	var _handlebars2 = _interopRequireDefault(_handlebars);

	var _util = __webpack_require__(9);

	var _util2 = _interopRequireDefault(_util);

	var _variables = __webpack_require__(14);

	exports['default'] = {
	    model: {
	        CurrentPage: 1,
	        PageSize: 10
	    },

	    events: {
	        'click th[order-field]': 'orderUpdate',
	        'click [cmd=query]': 'beforeQuery',
	        'click [cmd=page]': 'pageUpdate',
	        'click [cmd=selectAll]': 'selectAll',
	        'change [cmd=pageSize]': 'pageSizeUpdate',
	        'keypress .query-form input[name]': 'doQuery'
	    },

	    /**
	     * 预编译模板。
	     */
	    initialize: function initialize() {
	        if (this.templateId == null) {
	            this.template = function () {
	                return '';
	            };
	        } else {
	            this.template = _handlebars2['default'].compile((0, _jquery2['default'])(this.templateId).html());
	        }
	    },

	    render: function render() {
	        this.$el.html(this.template(this.model));
	        this.$('#tbpagesize').val(this.model.PageSize);
	        this.setOrderMark();
	        return this;
	    },

	    /**
	     * 按下 Enter 键时触发查询。
	     *
	     * @param {Event} e
	     * @private
	     */
	    doQuery: function doQuery(e) {
	        if (e != null && e.keyCode === 13) {
	            this.beforeQuery();
	            return false;
	        }
	    },

	    /**
	     * 使用条件查询时，重置当前页码。
	     *
	     * @private
	     */
	    beforeQuery: function beforeQuery() {
	        this.model.CurrentPage = 1;
	        this.query();
	    },

	    /**
	     * 查询方法，将查询条件发送到服务器，并调用 render 渲染返回数据。
	     *
	     * @abstract
	     */
	    query: function query() {
	        _util2['default'].error('"queryView.query" method must be implement.');
	    },

	    /**
	     * 每页显示数量被修改后触发。
	     *
	     * @param {Event} e
	     * @private
	     */
	    pageSizeUpdate: function pageSizeUpdate(e) {
	        var $target = this.$(e.currentTarget);
	        var pageSize = Number.parseInt($target.val(), 10);
	        if (isNaN(pageSize)) {
	            pageSize = 10;
	        }
	        this.model.PageSize = pageSize;
	        this.beforeQuery();
	    },

	    /**
	     * 获得分页参数。
	     *
	     * @returns {{pageNumber: number, pageSize: number, orderField: array}} 当前 View 的分页参数。
	     * @private
	     */
	    getPagination: function getPagination() {
	        var para = {};
	        para.pageNumber = this.model.CurrentPage;
	        para.pageSize = this.$('#tbpagesize').val();
	        para.orderField = this.model.OrderField;
	        return para;
	    },

	    /**
	     * 点击分页按钮后触发。
	     *
	     * @param {Event} e
	     * @private
	     */
	    pageUpdate: function pageUpdate(e) {
	        e.preventDefault();
	        var page = this.$(e.target).attr('arg');
	        if (page) {
	            page = Number.parseInt(page, 10);
	            this.model.CurrentPage = page;
	            this.query();
	        }
	        return;
	    },

	    /**
	     * 点击排序字段时触发。
	     *
	     * @param {Event} e
	     * @private
	     */
	    orderUpdate: function orderUpdate(e) {
	        var curOrder = ['', 'DESC'];
	        if (this.model.OrderField) {
	            curOrder = this.model.OrderField.split(' ');
	            if (curOrder.length === 1) {
	                curOrder.push('ASC');
	            }
	        }
	        var orderStr = this.$(e.currentTarget).attr('order-field').trim();
	        if (orderStr === '') {
	            return;
	        }

	        if (orderStr === curOrder[0]) {
	            curOrder[1] = curOrder[1] === 'ASC' ? 'DESC' : 'ASC';
	            this.model.OrderField = curOrder.join(' ');
	        } else {
	            this.model.OrderField = orderStr + ' ASC';
	        }

	        this.query();
	    },

	    /**
	     * 选择列表中所有的项。
	     *
	     * @param {Event} e
	     * @private
	     */
	    selectAll: function selectAll(e) {
	        var _this = this;
	        var $this = this.$(e.currentTarget);
	        var isChecked = $this.prop('checked');
	        _variables.slice.call(this.$('.item-select:not([disabled])')).forEach(function (item) {
	            _this.$(item).prop('checked', isChecked);
	        });
	    },

	    /**
	     * 设置排序图标。
	     *
	     * @private
	     */
	    setOrderMark: function setOrderMark() {
	        if (!this.model.OrderField) {
	            return;
	        }
	        var curOrder = ['', 'DESC'];
	        if (this.model.OrderField) {
	            curOrder = this.model.OrderField.split(' ');
	            if (curOrder.length === 1) {
	                curOrder.push('ASC');
	            }
	        }
	        this.$('.order-mark').remove().appendTo(this.$('[order-field=' + curOrder[0] + ']')).removeClass('fa-chevron-up fa-chevron-down').addClass(curOrder[1] === 'ASC' ? 'fa-chevron-up' : 'fa-chevron-down');
	    }
	};
	module.exports = exports['default'];

/***/ },
/* 19 */
/***/ function(module, exports) {

	/**
	 * 表格的添加一行，删除一行操作。
	 *
	 * @property {Object} _rowContainer 表格容器
	 * @property {Function} _rowTemplate 表格行的模板函数
	 *
	 * @module modules/tableRow
	 * @author FunTian
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
		value: true
	});
	exports['default'] = {
		_rowContainer: null,

		_rowTemplate: null,

		events: {
			'click [cmd="add-row"]': 'addTableRow',
			'click [cmd="remove-row"]': 'removeTableRow'
		},

		model: {
			_rowIndex: 1
		},

		initialize: function initialize() {
			this._rowContainer = this.$();
			this._rowTemplate = function () {
				return '';
			};
		},

		/**
	  * 在表格容器中添加一行。
	  *
	  * @param {Event} e
	  * @param {Object} model 渲染rowTemplate所需的数据，默认为this.model。
	  * @param {Number} model._rowIndex 维护的索引。
	  *
	  * @fires "row-added.base" 新行被添加到容器后触发。
	  */
		addTableRow: function addTableRow(e) {
			var model = arguments.length <= 1 || arguments[1] === undefined ? this.model : arguments[1];

			this._rowContainer.append(this._rowTemplate(model));

			this.trigger('row-added.base', model._rowIndex++);
		},

		/**
	  * 在表格容器中删除当前行。
	  *
	  * @param {Event} e
	  *
	  * @fires "row-removed.base" 行从表格容器移除后触发，此时 index 对应的行已经被删除。
	  */
		removeTableRow: function removeTableRow(e) {
			var $target = this.$(e.currentTarget);
			var $row = $target.parents('[data-index]');
			var index = $row.attr('data-index');
			var $rows = $row.nextAll();
			_.each($rows, function (item) {
				var $item = this.$(item);
				var index = Number.parseInt($item.attr('data-index'));
				$item.attr('data-index', index - 1);
			}, this);

			$row.remove();
			this.model._rowIndex--;
			this.trigger('row-removed.base', index);
		}
	};
	module.exports = exports['default'];

/***/ },
/* 20 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * 将 script[type="text/x-handlebars-template"] 的位置替换成相应模板渲染的结果。
	 *
	 * @module modules/templateAutoRender
	 * @author FunTian
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
	    value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _jquery = __webpack_require__(4);

	var _jquery2 = _interopRequireDefault(_jquery);

	var _underscore = __webpack_require__(5);

	var _underscore2 = _interopRequireDefault(_underscore);

	var _handlebars = __webpack_require__(17);

	var _handlebars2 = _interopRequireDefault(_handlebars);

	var _variablesSlice = __webpack_require__(15);

	var _variablesSlice2 = _interopRequireDefault(_variablesSlice);

	exports['default'] = {
	    /**
	     * 已缓存的模板编译结果，在初始化后填充该对象。
	     *
	     * @private
	     */
	    cachedTemplates: {},

	    /**
	     * 预编译并保存所有模板，会在所有模板上添加 data-template 属性，属性的值为缓存模板的 Id。
	     */
	    initialize: function initialize() {
	        var _this = this;

	        var $tpls = this.$('[type="text/x-handlebars-template"]');
	        _variablesSlice2['default'].call($tpls).forEach(function (item) {
	            var $item = _this.$(item);
	            var tpl = _handlebars2['default'].compile($item.html());
	            var id = $item.attr('id');
	            var isExclude = typeof $item.attr('data-exclude') !== 'undefined';
	            if (typeof id !== 'string') {
	                id = _underscore2['default'].uniqueId('cachedTpl_');
	                $item.attr('id', id);
	            }
	            _this.cachedTemplates[id] = tpl;
	            var $parent = $item.parent().attr('data-template', id);
	            if (isExclude) {
	                $parent.attr('data-exclude', '');
	            }
	        }, this);
	    },

	    /**
	     * 使用模板渲染结果替换模板。<br/>
	     *
	     * 替换所有 data-template 元素为模板的渲染结果。
	     */
	    autoRender: function autoRender() {
	        var _this2 = this;

	        var $tpls = this.$('[data-template]:not([data-exclude])');
	        _variablesSlice2['default'].call($tpls).forEach(function (item) {
	            var $item = _this2.$(item);
	            var id = $item.attr('data-template');
	            if (id == null) {
	                // 处理取出的 id 为 null 的情况。
	                return;
	            }
	            var $tpl = new _handlebars2['default'].SafeString(_this2.cachedTemplates[id](_this2.model)).string.trim();
	            $item.html($tpl);
	        }, this);
	    }
	};
	module.exports = exports['default'];

/***/ },
/* 21 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * 基于 jQuery UI Dialog 封装的工具类。
	 *
	 * @module messageManager
	 * @author FunTian
	 */

	'use strict';

	Object.defineProperty(exports, '__esModule', {
		value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _jquery = __webpack_require__(4);

	var _jquery2 = _interopRequireDefault(_jquery);

	var _underscore = __webpack_require__(5);

	var _underscore2 = _interopRequireDefault(_underscore);

	var _variables = __webpack_require__(14);

	exports['default'] = {
		/**
	  * dialog 函数的封装。
	  *
	  * @param {string} message 弹出层要显示的信息，HTML会被转义。
	  * @param {Object} options dialog 的配置，参见 {@link http://api.jqueryui.com/dialog/|jQuery UI Dialog}。
	  */
		showDialog: function showDialog(message, options) {
			if (message == null) {
				return;
			}
			var $dialog = (0, _jquery2['default'])('#common-dialog');

			(0, _jquery2['default'])('#common-dialog-txt').text(message);

			var opts = _underscore2['default'].extend({}, {
				title: '',
				width: 320,
				height: 250,
				modal: true,
				buttons: {
					'确定': function _() {
						$dialog.dialog('close');
					}
				}
			}, options);

			$dialog.dialog(opts);
		},
		/**
	  * alert和confirm的实现方法。
	  *
	  * @private
	  * @param {string} message 显示的信息。
	  * @param {Object} options dialog 的配置，参见 {@link http://api.jqueryui.com/dialog/|jQuery UI Dialog}。
	  * @param {{which: number, click: function}} options.buttons 应为一个包含 { which: number, click: function } 的对象。
	  * @param {function} options.close 如果点击右上角按钮关闭弹出框，将返回-1。
	  */
		_dialog: function _dialog(message, opts) {
			if (message === null || message === undefined) {
				message = String(message);
			}
			var $dialog = (0, _jquery2['default'])('#common-dialog');
			return new Promise(function (resolve, reject) {
				var buttons = opts.buttons;
				if (buttons != null) {
					for (var i = 0, j = i, len = buttons.length; i < len; i++) {
						var button = buttons[i];
						if (typeof button.which === 'undefined') {
							button.which = j++;
						}
						/* jshint loopfunc:true */
						button.click = (function (button) {
							return function () {
								resolve(button);
							};
						})(button);
					}
				}
				var close = opts.close;
				opts.close = function () {
					if (typeof close === 'function') {
						close();
					}
					resolve({
						which: -1
					});
				};

				(0, _jquery2['default'])('#common-dialog-txt').text(message);

				$dialog.dialog(opts);
			}).then(function (button) {
				$dialog.dialog('close').dialog('destroy');
				return button;
			});
		},
		/**
	  * 弹出信息提示用户（window.alert）。
	  *
	  * @param {string} message 显示的信息。
	  * @param {string=} title 弹出框的标题。
	  * @param {Object=} options dialog 的配置，参见 {@link http://api.jqueryui.com/dialog/|jQuery UI Dialog}。
	  * @return {Promise} 在用户关闭弹出框后被resolve的Promise。
	  */
		alert: function alert(message, title, options) {
			if (typeof title !== 'string' && options == null) {
				var _ref = [title, null];
				options = _ref[0];
				title = _ref[1];
			}

			var opts = {
				modal: true,
				buttons: [{
					text: '确定',
					primary: true
				}]
			};
			if (title != null) {
				opts.title = title;
			}
			return this._dialog(message, _underscore2['default'].extend({}, opts, options)).then(function () {
				return undefined;
			});
		},
		/**
	  * 让用户选择的弹出框（window.confirm）。
	  *
	  * @param {string} message 显示的信息。
	  * @param {string=} title 弹出框的标题。
	  * @param {Object=} options dialog 的配置，参见 {@link http://api.jqueryui.com/dialog/|jQuery UI Dialog}。
	  * @return {Promise} 在用户做出选择后被resolve的Promise，如果用户选择“确定”，则参数为true，否则为false。
	  */
		confirm: function confirm(message, title, options) {
			if (typeof title !== 'string' && options == null) {
				var _ref2 = [title, null];
				options = _ref2[0];
				title = _ref2[1];
			}

			var opts = {
				modal: true,
				buttons: [{
					text: '确定',
					which: 1,
					primary: true
				}, {
					text: '取消'
				}]
			};
			if (title != null) {
				opts.title = title;
			}
			return this._dialog(message, _underscore2['default'].extend({}, opts, options)).then(function (button) {
				if (button.which === 1) {
					return true;
				}
				return false;
			});
		}
	};
	module.exports = exports['default'];

/***/ },
/* 22 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, '__esModule', {
		value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var _jquery85ido = __webpack_require__(23);

	var _jquery85ido2 = _interopRequireDefault(_jquery85ido);

	exports['default'] = {
		_85: _jquery85ido2['default']
	};
	module.exports = exports['default'];

/***/ },
/* 23 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * 扩展 jQuery 的功能。
	 *
	 * @module plugins/postJSON
	 * @author Colin Niu
	 */

	"use strict";

	Object.defineProperty(exports, "__esModule", {
		value: true
	});

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

	var _jquery = __webpack_require__(4);

	var _jquery2 = _interopRequireDefault(_jquery);

	var _fixJsonDate = function _fixJsonDate(obj) {
		for (var key in obj) {
			if (obj.hasOwnProperty(key)) {
				var val = obj[key];
				switch (typeof val) {
					case "string":
						if (val.match(/^\/Date\(([\-\d]+)\)\/$/)) {
							obj[key] = new Date(Number.parseInt(RegExp.$1, 10));
						}
						break;
					case "object":
						_fixJsonDate(val);
						break;
					default:
						break;
				}
			}
		}
	};

	/**
	 * 使用 post 请求发送 JSON 并处理可能出现的日期转换问题。
	 *
	 * @param {string} url 请求的 Url。
	 * @param {Object} params 要发送给服务端的数据。
	 * @param {Object} jQuery ajax 配置。参见 {@link http://api.jquery.com/jQuery.ajax/|jQuery.ajax()}。
	 */
	var postJSON = function postJSON(url, params, options) {
		var optionSetup = {
			type: "POST",
			data: JSON.stringify(params),
			contentType: "application/json",
			converters: {
				"text json": function textJson(str) {
					var obj = _jquery2["default"].parseJSON(str);
					_fixJsonDate(obj);
					return obj;
				}
			}
		};
		var opt = _jquery2["default"].extend({}, optionSetup, options);
		return _jquery2["default"].ajax(url, opt).then(function(data) {
			if (data.error != null) {
				if (data.error.code === MessageCode.Unauth) {
					swal({
						title: '',
						text: data.error.message,
						showCancelButton: false,
						closeOnConfirm: false
					}, function() {
						location.href = __jspBasePath + 'login/';
					});
				}
			}
			return data;
		});
	};

	_jquery2["default"].extend({ 'postJSON': postJSON });

	exports["default"] = {
		postJSON: postJSON
	};
	module.exports = exports["default"];

/***/ }
/******/ ])
});
;
