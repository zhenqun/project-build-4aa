(function($) {
    var WHITESPACE_REG = /\s+/g;
    var SLICE = Array.prototype.slice;
    var KEYCODES = {
        ENTER: 13
    };

    /**
     * VisualForm 就是一个 form，但是在提交时会生成一个新的 form，通过该方法不触发浏览器的保存密码提示
     *
     * @param options 配置对象，配置可以通过 `vf-dashed-case` 在 form 上配置
     * @param {boolean} options.simulateEnter 是否模拟用户按下回车的行为
     * @param {boolean|object} options.trim 复制用户输入表单时是否进行 trim，默认 false。设置为 true 等同于 {ignore: [], trimAll: []}
     * @param {Array.<string>} options.trim.ignore 忽略字段的 key，不对这些字段做任何处理
     * @param {Array.<string>} options.trim.trimAll 不仅去掉两头空格，同时去掉中间空格的 key
     * @param {function} options.onBeforeSubmit 触发提交事件创建 VirtualForm，必须返回一个 boolean|Promise。当返回 Promise 的状态为 fulfilled 时或返回了一个 truth 时，会进行提交。
     * @fires submiited.visualForm 虚拟 form 被提交后触发
     * @return {jQuery}
     * @constructor
     *
     * @author WheelJS
     */
    function VisualForm(options) {
        var ins;
        if (typeof options === 'undefined') {
            return this;
        }
        else if (typeof options === 'string') {
            ins = this.data('visualForm');
            return ins[options](SLICE.call(arguments, 1));
        }
        ins = new VisualForm();

        ins.form = this;
        var trim = options.trim || false;
        if (trim === true) {
            trim = {
                ignore: [],
                trimAll: []
            };
        }
        else if (typeof trim === 'object') {
            trim = $.extend({
                ignore: [],
                trimAll: []
            }, trim);
        }
        ins.trim = trim;
        ins.onBeforeSubmit = options.onBeforeSubmit;
        ins.simulateEnter = options.simulateEnter || (typeof this.attr('vf-simulate-enter') !== 'undefined');

        ins.init();
        this.data('visualForm', ins);

        return this;
    }

    /**
     * 初始化 VisualForm，为 form 中的 .js-submit 点击绑定 form 提交事件；
     * 如果 `simulateEnter=true`，则也会为每个 input 绑定 keypress 事件，模拟 form 在 input 中的行为
     */
    VisualForm.prototype.init = function() {
        var _this = this;
        if (this.simulateEnter) {
            this.form.find('input[name]:not([type="hidden"])').on('keypress', function(e) {
                if (e.keyCode === KEYCODES.ENTER) {
                    _this.submit();
                }
            });
        }
        this.form.find('.js-submit').on('click', function() {
            _this.submit();
        });
    };

    /**
     * 用户点击 .js-submit 或按下回车触发，校验表单并提交
     */
    VisualForm.prototype.submit = function() {
        var _this = this;
        var validPromise = null;
        if (typeof this.onBeforeSubmit === 'function') {
            var beforeSubmit = this.onBeforeSubmit.call(this.form);
            if (typeof beforeSubmit.then === 'function') {
                validPromise = beforeSubmit;
            }
            else if (beforeSubmit) {
                validPromise = Promise.resolve();
            }
            else {
                validPromise = Promise.reject();
            }
        }
        else {
            validPromise = Promise.resolve();
        }
        validPromise.then(function(isValid) {
            _this._submit();
        });
    };

    VisualForm.prototype._submit = function() {
        var $form = this.createForm();
        $('body').append($form);
        $form.submit();
        this.form.trigger('submitted.visualForm');
    };

    VisualForm.prototype.createForm = function() {
        var $fields = this.form.find('input[name]');
        var trim = this.trim;

        var $form = $('<form>')
            .css('display', 'none')
            .attr({
                'method': this.form.attr('method'),
                'action': this.form.attr('action')
            });
        if (typeof trim === 'object') {
            for (var i = 0; i < $fields.length; i++) {
                var $field = $($fields[i]);
                var name = $field.attr('name');
                var value = $field.val();
                if (trim.ignore.indexOf(name) === -1) {
                    if (trim.trimAll.indexOf(name) > -1) {
                        value = value.replace(WHITESPACE_REG, '');
                    }
                    else {
                        value = $.trim(value);
                    }
                }
                var $hiddenField = $('<input>')
                    .attr({
                        type: 'hidden',
                        name: name
                    })
                    .val(value);
                $form.append($hiddenField);
            }
        }
        else {
            for (var i = 0; i < $fields.length; i++) {
                var $field = $($fields[i]);
                var $hiddenField = $('<input>')
                    .attr({
                        type: 'hidden',
                        name: $field.attr('name')
                    })
                    .val($field.val());
                $form.append($hiddenField);
            }
        }
        return $form;
    };

    $.fn.visualForm = VisualForm;
})(jQuery);
