(function (window) {
    toastr.options.positionClass='toast-middle-right';
    var DEFAULT_POST = {
        type: "POST",
        contentType: 'application/json',
        headers: {
            "content-type": "application/json"
        }
    };

    var urls = {
        validateMobile: '/mgmt/checkTelephoneRegister/',
        sendSms: '/sendMessage',
        first: '/forget/checkVerifyCode',
        second: '/forget/modifyPassword'
    };

    //area display controll
    var display = function (ele) {
        $(".area").each(function (idx, item) {
            $(item).addClass('hide');
            $(ele).removeClass('hide');
        })
    };

    //验证是否被使用
    function isUsed(url, val, errEleName, btn) {
        if (val != null && val != '') {
            $.get({
                url: url + val + '/',
                cache: false
            }).then(function (data) {
                if (data) {
                    $(errEleName).removeClass('hide');
                    $(btn).addClass('disabled').prop('disabled', true);
                }
                else {
                    $(errEleName).addClass('hide');
                    $(btn).removeClass('disabled').prop('disabled', false);
                }
            });
        }
        else {
            $(errEleName).addClass('hide');
            $(btn).removeClass('disabled').prop('disabled', false);
        }
    }

    $("[name='telephone']").on('blur', function () {
        var $telephone = $("[name='telephone']");
        var telephone = $.trim($telephone.val());
        if ($telephone.parsley().isValid()) {
            isUsed(urls.validateMobile, telephone, '#errtelePhone', '#next1,#sms-btn');
        }
        else {
            $('#errtelePhone').addClass('hide');
            $telephone.parsley().validate();
        }
    });

    var step1 = null;
    $('#firstForm').submit(function (e) {
        e.preventDefault();
        var $form = $('#firstForm');
        if ($form.parsley().isValid()) {
            var $btn = $('#next1');
            $btn.prop('disabled', true);
            var obj = $.extend($form.serializeJSON(), {
                type: 2001
            });
            $.ajax($.extend({}, DEFAULT_POST, {
                url: urls.first,
                data: JSON.stringify(trimField(obj))
            }))
            .then(
                function (data) {
                    if (data.flag === 'success') {
                        step1 = obj;
                        display('#second');
                    }
                    else {
                        $btn.prop('disabled', false);
                        toastr.error(data.message);
                    }
                },
                function() {
                    $btn.prop('disabled', false);
                }
            );
        }
    });

    $('#secondForm').submit(function (e) {
        e.preventDefault();
        var $form = $('#secondForm');
        if ($form.parsley().isValid() && step1 != null) {
            var $btn = $('#next2');
            var obj = JSON.stringify($.extend($form.serializeJSON(), step1));
            $btn.prop('disabled', true);
            $.ajax($.extend({}, DEFAULT_POST, {
                url: urls.second,
                data: obj
            }))
            .then(
                function (data) {
                    if (data.flag === 'success') {
                        display('#third');
                    }
                    else {
                        $btn.prop('disabled', false);
                        toastr.error(data.message);
                    }
                },
                function() {
                    $btn.prop('disabled', false);
                }
            );
        }
    });

    var sendMes = function (e) {
        e.preventDefault();
        var self = $(this);
        var secLeft = self.data("seconds");
        var $telephone = $("[name='telephone']");
        var tel = $telephone.val();
        if (self.hasClass('disabled')) {
            return;
        }
        if (!$telephone.parsley().isValid()) {
            $telephone.parsley().validate()
            return;
        }

        if (secLeft === undefined || secLeft === -1) {
            secLeft = 60;
            var $text = $('a', self);
            $text.text(secLeft + "s");
            self.data("seconds", secLeft);
            var timer = setInterval(function () {
                secLeft--;
                if (secLeft === 0) {
                    self.data("seconds", -1);
                    $text.text("重新发送");
                    clearInterval(timer);
                } else {
                    $text.text(secLeft + "s");
                }
            }, 1000);


            $.ajax($.extend({}, DEFAULT_POST, {
                url: urls.sendSms,
                data: JSON.stringify({
                    telephone: tel,
                    type: '2001'
                })
            })).then(function (data) {
                if (data.flag == 'fail') {
                    toastr.error(data.message);
                }
            });
        }
    };


    $('#firstForm').parsley();
    $('#secondForm').parsley();

    $('body').on('click', "#sms-btn", sendMes);
}())