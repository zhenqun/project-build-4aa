(function (window) {
    toastr.options.positionClass = 'toast-middle-right';
    var DEFAULT_POST = {
        type: 'POST',
        headers: {
            'content-type': 'application/json'
        }
    };

    var urls = {
        first: '/sso/verifyRetrievePwdVerificationCode',
        second: '/sso/retrievePasswordForWeb',
        verifyTele: '/sso/common/checkPhoneRegist/',
        checkCode: 'checkValidateCode/'
    };

    var userId = '';

    //area display controll
    var display = function (ele) {
        $(".area").each(function (idx, item) {
            $(item).addClass('hide');
            $(ele).removeClass('hide');
        });
    };

    //验证是否被使用
    function isUsed(url, val, errEleName) {
        var $errEle = $(errEleName);
        $errEle.addClass('hide');
        if (val != null && val != '') {
            $.get(url + val + '/', { t: new Date().getTime() }).then(function (data) {
                if (!data) {
                    $errEle.removeClass('hide');
                }
                else {
                    $errEle.addClass('hide');
                }
            });
        }
    }

    $(window).on('load', function() {
        var $firstForm = $('#firstForm');
        var $secondForm = $('#secondForm');

        $firstForm.parsley();
        $firstForm.on('submit', function (e) {
            e.preventDefault();
            var $firstForm = $(this);
            if (!$firstForm.parsley().isValid()) {
                $firstForm.parsley().validate();
                return;
            }

            var obj = JSON.stringify(
                trimField($firstForm.serializeJSON())
            );
            $.ajax($.extend({}, DEFAULT_POST, {
                url: urls.first,
                data: obj
            })).then(function(data) {
                if (data.flag === 'success') {
                    userId = data.message;
                    display('#second');
                }
                else {
                    toastr.error(data.message);
                }
            });
        });

        $secondForm.parsley();
        $secondForm.on('submit', function (e) {
            e.preventDefault();
            var $secondForm = $(this);
            if (!$secondForm.parsley().isValid()) {
                $secondForm.parsley().validate();
                return;;
            }
            if (userId != '') {
                var obj = JSON.stringify($.extend(
                    trimField($secondForm.serializeJSON()),
                    {
                        userId: userId
                    }
                ));

                $.ajax($.extend({}, DEFAULT_POST, {
                    url: urls.second,
                    data: obj
                })).then(function (data) {
                    if (data.flag === 'success') {
                        display('#third');
                    }
                    else {
                        toastr.error(data.message);
                    }
                });
            }
        });

        $('[name="validateCode"]').on('blur', function () {
            isUsed(urls.checkCode, $.trim($(this).val()), '#errvalidCode');
        });

        $('[name="telePhone"]').on('blur', function() {
            isUsed(urls.verifyTele, $.trim($(this).val()), '#errtelePhone');
        });

        $('body').on('click', '#sms-btn', function (e) {
            e.preventDefault();

            var sendUrl = '/sso/sendMessage';
            var self = $(this);
            var secLeft = self.data('seconds');
            var $telePhone = $("[name='telePhone']");
            var $validateCode = $('[name="validateCode"]');

            var tel = $.trim($telePhone.val());
            if (!$telePhone.parsley().isValid() || !$validateCode.parsley().isValid()) {
                $telePhone.parsley().validate();
                $validateCode.parsley().validate();
                return;
            }
            var sendMesAction = function () {
                if (secLeft === undefined || secLeft === -1) {
                    secLeft = 120;
                    self.text(secLeft + 's');
                    self.data('seconds', secLeft);
                    var timer = setInterval(function () {
                        secLeft--;
                        if (secLeft === 0) {
                            self.data('seconds', -1);
                            self.text('重新发送');
                            clearInterval(timer);
                        } else {
                            self.text(secLeft + "s");
                        }
                    }, 1000);

                    $.ajax($.extend({}, DEFAULT_POST, {
                        url: sendUrl,
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

            $.get(urls.checkCode + $.trim($validateCode.val()) + '/', { t: new Date().getTime() })
                .then(function (data) {
                    if (!data) {
                        $('#errvalidCode').removeClass('hide');
                        return;
                    }
                    else {
                        $('#errvalidCode').addClass('hide');
                        return $.get(urls.verifyTele + tel + '/', { t: new Date().getTime() });
                    }
                })
                .then(function (data) {
                    if (!data) {
                        $('#errtelePhone').removeClass('hide');
                    }
                    else {
                        $('#errtelePhone').addClass('hide');
                        sendMesAction();
                    }
                });
        });
    });
}(window));
