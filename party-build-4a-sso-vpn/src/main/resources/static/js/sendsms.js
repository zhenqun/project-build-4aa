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
                if (self.hasClass('disabled')) {
                    return;
                }
                var url = '/sso/sendMessage';
                var secLeft = self.data('seconds');
                var $telePhone = $('[name="telePhone"]');
                var $validateCode = $('input[name="validateCode"]');

                var tel = $.trim($telePhone.val());
                if (!$telePhone.parsley().isValid()) {
                    $telePhone.parsley().validate();
                    return;
                }
                if (!$validateCode.parsley().isValid()) {
                    $validateCode.parsley().validate();
                    return;
                }
                if (secLeft === undefined || secLeft === -1) {
                    secLeft = 60;
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
