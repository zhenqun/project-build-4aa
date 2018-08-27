var CryptoAgent;

function OnLoad() {
    try {
        var eDiv = document.createElement("div");
        var chromestr = "Chrome/";
        var indexOpr = navigator.appVersion.indexOf("OPR");
        var index = navigator.appVersion.indexOf(chromestr);
        
        if (index >= 0 && indexOpr < 0) {
          var ChromeVersion = navigator.appVersion.substring(index + chromestr.length, index + chromestr.length + 2);
          if(ChromeVersion > 41)
          {
            CryptoAgent = new nmCryptokit();
          }
          else
          {
              alert("仅支持Chrome.");
          }
        }
        else
        {
          CryptoAgent = new Cryptokit();
        }
        CryptoAgent.init();
    }
    catch (e) {
        alert(e);
        return;
    }
}

function OnUnLoad() {
    try {
        CryptoAgent.uninit();
    }
    catch (e) {
        alert(e);
        return;
    }
}

function GetLastErrorDescAsync() {
	var def = $.Deferred();
	CryptoAgent.GetLastErrorDesc(function(result) {
		def.reject(result.value);
	});
	return def;
}

function GetSignCertInfoAsync(infoType) {
	var def = $.Deferred();
	CryptoAgent.GetSignCertInfo(infoType, function(result) {
		if (result.error == 0) {
			def.resolve(result.value);
		}
		else {
			GetLastErrorDescAsync()
				.then(null, function(error) {
					def.reject(error);
				});
		}
	});
	return def;
}

function SelectCertificateAsync(arg1, arg2, arg3, arg4) {
	var def = $.Deferred();
	CryptoAgent.SelectCertificate(arg1, arg2, arg3, arg4, function(result) {
		if (result.error == 0) {
			def.resolve(result.value);
		}
		else {
			GetLastErrorDescAsync()
				.then(null, function(error) {
					def.reject(error);
				});
		}
	});
	return def;
}

function SignMsgPKCS7Async(source, alg, isWithSource) {
	var def = $.Deferred();
	CryptoAgent.SignMsgPKCS7(source, alg, isWithSource, function callback(result) {
        if (0 == result.error) {
        	def.resolve(result.value);
        }
        else {
        	GetLastErrorDescAsync()
				.then(null, function(error) {
					def.reject(error);
				});
        }
    });
	return def;
}

function getIDCard() {
	var source = new Date().toISOString();
	var selectedAlg = 'SHA-1';
	var IsWithSource = true;
	
}

function caLogin(){
	var data = {};
	SelectCertificateAsync('', '', '', '')
		.then(
			function(result) {
				return GetSignCertInfoAsync('SubjectCN');
			},
			function(error) {
				alert(error);
				var def = $.Deferred();
				def.reject(error);
				return def;
			}
		)
		.then(function(result) {
			if (result) {
				var results = result.split('@');
				if (results.length > 0) {
					data.idCard = results[results.length - 2];
				}
			}
			var source = new Date().toISOString();
			data.source = source;
			return SignMsgPKCS7Async(source, 'SHA-1', true);
		})
		.then(function(result) {
			data.sign = result;
			return data;
		})
		.then(function() {
			var $loginForm = $('#loginForm');
			$('[name="loginType"]', $loginForm).val('ca');
			for (var key in data) {
				if (data.hasOwnProperty(key)) {
					$('#ca-' + key).val(data[key]);
				}
			}
			var loginForm = $loginForm[0];
			if (loginForm) {
				loginForm.submit();
			}
		})
//		.then(function(result) {
//			console.warn(result);
//		});
}



// Select certificate
function SelectCertificateOnClick() {
    document.getElementById("SelectCertResult").value = "false";

    var subjectDNFilter = "";
    var issuerDNFilter = "";
    var serialNumFilter = "";
    subjectDNFilter = document.getElementById("SubjectDNFilter").value;
    issuerDNFilter = document.getElementById("IssuerDNFilter").value;
    serialNumFilter = document.getElementById("SerialNumFilter").value;

    CryptoAgent.selectCertificate(subjectDNFilter, issuerDNFilter, serialNumFilter,
        function callback(result) {
            if (0 == result.error) {
                document.getElementById("SelectCertResult").value = result.value;
            }
            else {
                CryptoAgent.GetLastErrorDesc(GetLastErrorCallback);
            }
        });
}