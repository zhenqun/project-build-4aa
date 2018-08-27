(function (window) {
    toastr.options.positionClass = 'toast-middle-right';
    var DEFAULT_POST = {
        type: "POST",
        headers: {
            "content-type": "application/json"
        },
    }

    var urls = {
        first: '/sso/verifyRetrievePwdVerificationCode',
        second: '/sso/retrievePasswordForWeb',
        verifyTele: '/sso/common/checkPhoneRegist/'
    }

    var userId = '';

    //area display controll
    var display = function (ele) {
        $(".area").each(function (idx, item) {
            $(item).addClass('hide');
            $(ele).removeClass('hide');
        })
    };

    $('#firstForm').submit(function (e) {
        e.preventDefault();
        if ($('#firstForm').parsley().isValid()) {
            var obj = JSON.stringify($('#firstForm').serializeJSON());
            $.ajax($.extend(DEFAULT_POST, {
                url: urls.first,
                data: obj,
                success: function (data) {
                    if (data.flag === 'success') {
                        userId = data.message;
                        display('#second');
                    }
                    else {
                        toastr.error(data.message);
                    }
                }
            }))
        }
    });

    $('#secondForm').submit(function (e) {
        e.preventDefault();
        if ($('#secondForm').parsley().isValid() && userId != '') {
            var obj = JSON.stringify($.extend($('#secondForm').serializeJSON(), {
                userId: userId
            }));

            $.ajax($.extend(DEFAULT_POST, {
                url: urls.second,
                data: obj,
                success: function (data) {
                    if (data.flag === 'success') {
                        display('#third');
                    }
                    else {
                        toastr.error(data.message);
                    }
                }
            }))
        }
    });



    // //验证手机号是否
    // $("[name='telePhone']").on('blur', function () {

    // });



    var sendMes = function (e) {
        var sendUrl = '/sso/sendMessage';
        e.preventDefault();
        var self = $(this);
        var secLeft = self.data("seconds");
        var type = '2001';
        var tel = $("[name='telePhone']").val();
        var t = new RegExp(/^1[3456789]\d{9}$/);
        if (!$('[name="telePhone"]').parsley().isValid()) {
            $('[name="telePhone"]').parsley().validate()
            return;
        }
        //验证是否被使用
        function isUsed(url, val, errEleName) {
            if (val != null && val != '') {
                $.get(url + val).then(function (data) {
                    if (!data) {
                        $(errEleName).removeClass('hide');
                        return;
                        // $("[value='注册']").prop('disabled', true);
                    }
                    else {
                        $(errEleName).addClass('hide');
                        // $("[value='注册']").prop('disabled', false);
                        if (secLeft === undefined || secLeft === -1) {
                            secLeft = 60;
                            self.text(secLeft + "s");
                            self.data("seconds", secLeft);
                            var timer = setInterval(function () {
                                secLeft--;
                                if (secLeft === 0) {
                                    self.data("seconds", -1);
                                    self.text("重新发送");
                                    clearInterval(timer);
                                } else {
                                    self.text(secLeft + "s");
                                }
                            }, 1000);

                            //TODO: send ajax request
                            var sendSms = function () {
                                $.ajax({
                                    type: "POST",
                                    url: sendUrl,
                                    contentType: "application/json",
                                    data: JSON.stringify({
                                        telephone: tel,
                                        type: type
                                    })
                                }).then(function (data) {
                                    if (data.flag == 'fail') {
                                        toastr.error(data.message);
                                    }
                                })
                            };
                            sendSms();
                        }

                    }
                })
            }
            else {
                $(errEleName).addClass('hide');
                $("[value='注册']").prop('disabled', false);
            }
        };
        isUsed(urls.verifyTele, $("[name='telePhone']").val(), '#errtelePhone');

    };



    $('#firstForm').parsley();
    $('#secondForm').parsley();

    $('body').on('click', "#sms-btn", sendMes);

}())