(function($) {
    $(window).on('load', function() {
        $('#sms-btn')
            .on('change.validation', function() {
                var $this = $(this);
                var isTelephoneValid = $this.prop('telephone-valid');
                var isCaptchaValid = $this.prop('captcha-valid');
                if (isTelephoneValid && isCaptchaValid) {
                    $this.removeClass('disabled');
                }
                else {
                    $this.addClass('disabled');
                }
            })
            .on('click', function (e) {
                e.preventDefault();

                var self = $(this);
                var url = '/sso/sendMessage';
                var secLeft = self.data('seconds');
                var tel = $.trim($('[name="telePhone"]').val());

                //图片验证码和手机号校验不通过返回
                var teleErr = !$('[name="telePhone"]').parsley().isValid() || false;
                var codeErr = !$('[name="validateCode"]').parsley().isValid() || false;
                if (teleErr || codeErr) {
                    if (teleErr) {
                        $('[name="telePhone"]').parsley().validate();
                    }
                    if (codeErr) {
                        $('[name="validateCode"]').parsley().validate();
                    }
                    return;
                }
                if (self.hasClass('disabled')) {
                    return;
                }
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
                            self.text(secLeft + 's');
                        }
                    }, 1000);

                    var clear = self.attr('data-clear');
                    var $clear = $(clear);
                    $.ajax({
                        type: 'POST',
                        url: url,
                        contentType: 'application/json',
                        data: JSON.stringify({
                            telephone: tel,
                            type: '2000'
                        })
                    }).then(function (data) {
                        if (data.flag === 'fail') {
                            toastr.error(data.message);
                        }
                        if ($clear.length > 0) {
                            refresh();
                            $clear.val('').select();
                        }
                    });
                }
            });
    });
})($);
