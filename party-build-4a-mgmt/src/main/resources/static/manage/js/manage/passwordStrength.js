/**
 * 将 zxcvbn 和 Parsley 组合，创建 Parsley 自定义验证器，以及密码强度显示组件的事件绑定
 */
(function($) {
    var PASSWORD_STRATEGIES = [
        {
            text: '太弱',
            className: 'text-danger'
        },
        {
            text: '弱',
            className: 'text-danger'
        },
        {
            text: '一般',
            className: 'text-info'
        },
        {
            text: '强',
            className: 'text-primary'
        },
        {
            text: '极强',
            className: 'text-success'
        }
    ];

    if (typeof Parsley !== 'undefined') {
        Parsley.addValidator('passwordStrength', {
            requirementType: 'string',
            validateString: function(val, strengthLevel) {
                var strength = zxcvbn(val);
                return strength.score >= strengthLevel;
            }
        });
    }

    if (typeof $.validator !== 'undefined') {
        $.validator.addMethod('passwordStrength', function(val, el, strength) {
            return this.optional(el)  || (zxcvbn(val).score >= strength);
        });
    }

    $(window).on('load', function() {
        $('.js-password-strength').on('keyup', function(e) {
            var pwd = $(this).val();
            var strength = zxcvbn(pwd);
            var strengthResult = PASSWORD_STRATEGIES[strength.score];
            if (strengthResult) {
                $('.js-pwd-strength-result')
                    .attr('class', 'js-pwd-strength-result')
                    .text(strengthResult.text)
                    .addClass(strengthResult.className);
            }
        });
    });
})($);
