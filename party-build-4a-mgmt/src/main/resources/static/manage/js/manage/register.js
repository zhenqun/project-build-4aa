(function () {
    toastr.options.positionClass = 'toast-middle-right';
    
    function refresh() {
        $('#yanzhengma').attr('src', 'validateCodeServlet?w=96&h=42&t=' + Math.random());
    }
    window.refresh = refresh;

    var urls = {
        register: '/user/register',
        getOrg: '/common/getorg-dicts',
        checkTel: '/mgmt/checkTelephoneRegister/',
        checkCode: '/checkValidateCode/',
        sendSms: '/sendMessage'
    };

    /**
     * 验证是否被使用
     * @param url
     * @param val
     * @param {string} errEleName 错误提示元素 selector
     * @param {string} btnToDisable 要禁用的按钮 selector，使用逗号分隔
     */
    function isUsed(url, val, errEleName, btnToDisable) {
        if (val != null && val.length > 0) {
            return $.get(url + val + '/')
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

    $(window).on('load', function() {
        refresh();
        $('[data-toggle="popover"]').popover();

        $('#regForm').parsley();

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

        $('[name="validateCode"]').on('blur', function () {
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
        });

        $('body').on('click', '#submitBtn', function () {
            var $form = $('#regForm');
            if ($('#regForm').parsley().validate()) {
                var obj = $form.serializeJSON();
                obj = trimField(obj, {
                    ignore: ['password', 'rPassword', 'vpnpwd', 'revpnpwd'],
                    trimAll: ['name', 'idCard']
                });
                var $btn = $('#submitBtn');
                $btn.val('正在激活').prop('disabled', true);
                $.ajax({
                    type: "POST",
                    url: urls.register,
                    contentType: "application/json",
                    data: JSON.stringify(obj),
                    success: function (data) {
                        if (data.flag === 'success') {
                            toastr.success('激活成功');
                            setTimeout(function () {
                                location.href = 'login';
                            }, 2000)
                        }
                        else if (data.flag === 'suggest') {
                            $btn.val('激 活').prop('disabled', false);
                            toastr.error(null, '系统未为您分配安全中心账号或者您的授权码错误，如果您是管理员，请点击该提示消息前往虚拟专网区激活', {
                                onclick: function() {
                                    location.href = VPNURL + 'register';
                                },
                                timeOut: 30e3,
                                extendedTimeOut: 20e3
                            });
                        }
                        else {
                            $btn.val('激 活').prop('disabled', false);
                            toastr.error(data.message);
                        }
                    },
                    error: function(data) {
                        $btn.val('激 活').prop('disabled', false);
                        toastr.error('服务器内部错误，请联系技术人员');
                    }
                })
            }
        });
    });

})();
