(function () {
    toastr.options.positionClass = 'toast-middle-right';

    var urls = {
        register: '/sso/internet/register',
        checkEmail: '/sso/internet/checkEmailRegister/',
        checkName: '/sso/internet/checkUsernameRegister/',
        checkTel: '/sso/internet/checkTelephoneRegister/',
        checkCode: 'checkValidateCode/'
    };

    function agree() {
        $('[name="protocal"]').prop('checked', true);
        $('#protocal').addClass('hide');
    }

    function refresh() {
        $('#yanzhengma').attr('src', 'validateCodeServlet?w=96&h=42&t=' + (Math.random() * 10));
    }
    refresh();
    window.refresh = refresh;

    /**
     * 验证是否被使用
     * @param url
     * @param val
     * @param {string} errEleName 错误提示元素 selector
     * @param {string} btnToDisable 要禁用的按钮 selector，使用逗号分隔
     */
    function isUsed(url, val, errEleName, btnToDisable) {
        if (val != null && val.length > 0) {
            return $.get(url + val + '/', { t: new Date().getTime() })
                .then(function (data) {
                    if (!data) {
                        $(errEleName).removeClass('hide');
                    }
                    else {
                        $(errEleName).addClass('hide');
                    }
                    if (btnToDisable) {
                        var classOperator = data ? 'removeClass' : 'addClass';
                        $(btnToDisable)
                            .prop('disabled', !data)
                            [classOperator]('disabled');
                    }

                    return data;
                });
        }
        else {
            $(errEleName).addClass('hide');
            if (btnToDisable) {
                $(btnToDisable)
                    .prop('disabled', false)
                    .removeClass('disabled');
            }
            var def = $.Deferred();
            def.resolve(true);
            return def.promise();
        }
    }

    // click to add an extra account
    function extend() {
        var n = 0;
        var $btn = $('#extend');
        var $con = $btn.parent('.form-con');
        return function () {
            n++;
            $con.append([
                '<div class="register-user-box clearfix register-sfzh">',
                    '<span class="reg-label">拓展账号' + n + '</span>',
                    '<input name="extend" type="text" data-parsley-maxlength="20" data-parsley-maxlength-message="拓展账号最多20个字符" data-parsley-errors-container="#account' + n + '">',
                    '<p class="text" id="account' + n + '"></p>',
                '</div>'
            ].join(""));
        }
    }

    $(window).on('load', function() {
        $('#regForm').parsley();

        $('body').on('submit', '#regForm', function (e) {
            e.preventDefault();

            var $form = $('#regForm');
            var $btn = $('#submitBtn');
            if (!$form.parsley().isValid() || $btn.hasClass('js-submitting')) {
                $form.parsley().validate();
                return;
            }

            var obj = $form.serializeJSON();
            var extendAccounts = [];
            var $extends = $('[name="extend"]');
            if ($extends.length > 0) {
                extendAccounts = Array.from($extends, function(e) {
                    return $(e).val();
                }).filter(function(val) {
                    return val != null && val.length > 0;
                });
            }

            obj.uniqueKey = extendAccounts;
            $btn
                .addClass('js-submitting')
                .val('正在注册');
            $.ajax({
                type: "POST",
                url: urls.register,
                contentType: "application/json",
                data: JSON.stringify(
                    trimField(obj, {
                        ignore: ['password', 'rPassword'],
                        trimAll: ['name', 'idCard']
                    })
                )
            })
            .then(
                function(data) {
                    $btn
                        .removeClass('js-submitting')
                        .val('注 册');
                    if (data.flag === 'success') {
                        toastr.success(null, '注册成功，登录时用户名请填写注册时的手机号码或者身份证号码', {
                            timeOut: 5e3,
                            extendedTimeOut: 3e3
                        });
                        setTimeout(function () {
                            location.href = 'login';
                        }, 2000);
                    }
                    else {
                        toastr.error(data.message);
                    }
                },
                function() {
                    $btn
                        .removeClass('js-submitting')
                        .val('注 册');
                    toastr.error('服务器内部错误');
                }
            );

            return false;
        });

        $('body').on('click', '[name="protocal"]', function () {
            var isAgreeProtocol = $('[name="protocal"]').prop('checked');
            if (isAgreeProtocol) {
                $('#protocal').addClass('hide');
            }
            else {
                $('#protocal').removeClass('hide');
            }
            $('#submitBtn').prop('disabled', !isAgreeProtocol);
        });

        $('body').on('click', '#yanzhengma', refresh);

        $('body').on('click', "#extend", extend());

        $('[name="telePhone"]').on('blur', function () {
            var $smsBtn = $('#sms-btn');
            $smsBtn
                .removeProp('telephone-valid')
                .trigger('change.validation');
            isUsed(urls.checkTel, $.trim($(this).val()), '#errtelePhone', '#submitBtn')
                .then(function(data) {
                    $smsBtn
                        .prop('telephone-valid', data)
                        .trigger('change.validation');
                });
        });

        $('[name="validateCode"]').on('input', _.debounce(function(){
            var $smsBtn = $('#sms-btn');
            $smsBtn
                .removeProp('captcha-valid')
                .trigger('change.validation');
            isUsed(urls.checkCode, $.trim($(this).val()), '#errvalidCode', '#submitBtn')
                .then(function(data) {
                    $smsBtn
                        .prop('captcha-valid', data)
                        .trigger('change.validation');
                });
        },100));
    });
})();
