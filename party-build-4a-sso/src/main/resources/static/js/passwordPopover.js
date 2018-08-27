// CapsLock.js http://code.stephenmorley.org/javascript/detecting-the-caps-lock-key/
var CapsLock = function() {

    var IS_MAC = /Mac/.test(navigator.platform);

    var capsLock = false;
    var listeners = [];

    // Returns whether caps lock currently appears to be on.
    function isOn(isOn) {
        if (typeof isOn === 'undefined') {
            return capsLock;
        }
        capsLock = isOn;
    }

    // Adds a listener. When a change is detected in the status of the caps lock
    // key the listener will be called with the value true if caps lock is now on
    // and false if caps lock is now off. The parameter is:
    //
    // listener - the listener
    function addListener(listener) {
        listeners.push(listener);
    }

    // Handles a key press event. The parameter is:
    //
    // e - the event
    function handleKeyPress(e) {
        var charCode = e.charCode;
        var shiftKey = e.shiftKey;
        var priorCapsLock = capsLock;

        if (charCode >= 97 && charCode <= 122) {
            capsLock = shiftKey;
        } else if (charCode >= 65 && charCode <= 90 && !(shiftKey && IS_MAC)) {
            capsLock = !shiftKey;
        }

        if (capsLock !== priorCapsLock) {
            listeners.forEach(function (listener) {
                return listener(capsLock);
            });
        }
    }

    function handleKeyUp(e) {
        var priorCapsLock = capsLock;

        if (typeof e.getModifierState === 'function') {
            capsLock = e.getModifierState('CapsLock');
        }

        if (capsLock !== priorCapsLock) {
            listeners.forEach(function (listener) {
                return listener(capsLock);
            });
        }
    }

    window.addEventListener('keypress', handleKeyPress);
    window.addEventListener('keyup', handleKeyUp);

    return { isOn: isOn, addListener: addListener };
}();

(function($) {
    var UPPER_REG = /[A-Z]/;
    var LOWER_REG = /[a-z]/;
    var NUMERIC_REG = /[0-9]/;
    function updateCheckResult($password) {
        var val = $.trim($password.val());
        var checkResult = $password.data('checkResult');
        if (typeof checkResult === 'undefined') {
            checkResult = {};
        }
        checkResult.length = val.length >= 8 && val.length <= 18;
        checkResult.upper = UPPER_REG.test(val);
        checkResult.lower = LOWER_REG.test(val);
        checkResult.letter = checkResult.upper || checkResult.lower;
        checkResult.numeric = NUMERIC_REG.test(val);
        for (var key in checkResult) {
            if (checkResult.hasOwnProperty(key)) {
                var result = checkResult[key];
                var $result = $('.js-check-result [data-check-result="' + key + '"]');
                if (result) {
                    $result
                        .addClass('text-success')
                        .find('i.fa')
                        .removeClass('fa-circle-thin')
                        .addClass('fa-check-circle-o');
                }
                else {
                    $result
                        .removeClass('text-success')
                        .find('i.fa')
                        .removeClass('fa-check-circle-o')
                        .addClass('fa-circle-thin');
                }
            }
        }

        $password.data('checkResult', checkResult);
    }

    $(window).on('load', function() {
        $('.js-password-popover')
            .popover({
                container: 'body',
                content: $('#password-popover-tpl').html(),
                html: true,
                placement: 'top',
                trigger: 'manual'
            })
            .on('keyup', function() {
                updateCheckResult($(this));
            })
            .on('focus', function() {
                $(this).popover('show');
            })
            .on('shown.bs.popover', function(e) {
                updateCheckResult($(this));
            })
            .on('blur', function() {
                $(this).popover('hide');
            });

        CapsLock.addListener(function(flag) {
            Array.from($('.js-password-capslock')).forEach(function(el) {
                var $el = $(el);
                var isFocusing = $el.data('focusing');
                if (isFocusing) {
                    if (flag) {
                        $el.tooltip('show');
                    }
                    else {
                        $el.tooltip('hide');
                    }
                }
            });
        });

        $('.js-password-capslock')
            .tooltip({
                container: 'body',
                title: '大写锁定已打开',
                placement: 'right',
                trigger: 'manual'
            })
            .on('focus', function() {
                var $this = $(this);
                $this.data('focusing', true);
                if (CapsLock.isOn()) {
                    $this.tooltip('show');
                }
            })
            .on('blur', function() {
                var $this = $(this);
                $this
                    .tooltip('hide')
                    .data('focusing', false);
                CapsLock.isOn(false);
            });
    });
})(jQuery);
