 var extensionsID = "cifnoojhdkmogkjnmcafbepnjmaohdee";
 var nativeHostName = "com.cfca.cryptokit.sddr";
 var clsid = "";

var extensionErr = "Extension doesn't exist.";
var hostErr = "Host doesn't exist.";

function nmCryptokit() {
};


nmCryptokit.prototype.init = function () {
try{
    var paramArr = new Array();

    var param1 = new Object();
    param1.param = clsid;
    paramArr.push(param1);

    var msgJSON = new Object();
    msgJSON.function = "CreateCOMObj";
    msgJSON.params = paramArr;

    var requestJSON = new Object();
    requestJSON.type = "connect";
    requestJSON.host = nativeHostName;
    requestJSON.message = msgJSON;
    chrome.runtime.sendMessage(extensionsID, requestJSON,
         function (response) { 
				if("undefined" != chrome.runtime.lastError){
                    console.error(chrome.runtime.lastError.message);
                    alert(extensionErr);
                }            
         });
         }
	catch(e)
	{
		alert(e);
		throw e;
	}
}


nmCryptokit.prototype.uninit = function () {

        var requestJSON = new Object();
        var msgJSON = new Object();
        var paramArr = new Array();
        var param1 = new Object();

        param1.param = clsid;
        paramArr.push(param1);
                        
        msgJSON.function = "ReleaseCOMObj";
        msgJSON.params = paramArr;
                        
        requestJSON.type = "disconnect";
        requestJSON.host = nativeHostName;
        requestJSON.message = msgJSON;

        chrome.runtime.sendMessage(extensionsID, requestJSON,
        function (response) { 
        });
}

nmCryptokit.prototype.SelectCertificate = function (bstrSubjectDNFilter, bstrIssuerDNFilter, bstrSerialNumFilter, bstrCSPNameList, callback) {
          
       var requestJSON = new Object();
       var msgJSON = new Object();
       var paramArr = new Array();
       
       var dnFilter = new Object();
       dnFilter.param = bstrSubjectDNFilter;
       paramArr.push(dnFilter);
       
       var issuerDNFilter = new Object();
       issuerDNFilter.param = bstrIssuerDNFilter;
       paramArr.push(issuerDNFilter);
          
       var serialNumFilter = new Object();
       serialNumFilter.param = bstrSerialNumFilter;
       paramArr.push(serialNumFilter);
    
       var cspNameList = new Object();
       cspNameList.param = bstrCSPNameList;
       paramArr.push(cspNameList);
                                  
       msgJSON.function = "SelectCertificate";
       msgJSON.params = paramArr;
       
       requestJSON.type = "SelectCertificate";
       requestJSON.message = msgJSON;           
       
       chrome.runtime.sendMessage(extensionsID, requestJSON,
            function (response) { 
                var result = new Object();                
                if(0 == response.errorcode){ 
                    result.error = 0;                     
                    result.value = response.result;                                     
                }
                else if(1 == response.errorcode){
                    result.error = response.errorcode;  
                    result.value = hostErr;
                }
                else{
                    result.error = response.errorcode;  
                    result.value = "sendMessage response error!";
                }
                callback(result);
	        });    
}


nmCryptokit.prototype.GetSignCertInfo = function (bstrInfoType, callback) {
       var requestJSON = new Object();
       var msgJSON = new Object();
       var paramArr = new Array();

       var infoType = new Object();
       infoType.param = bstrInfoType;
       paramArr.push(infoType);
      
       msgJSON.function = "GetSignCertInfo";
       msgJSON.params = paramArr;
       
       requestJSON.type = "GetSignCertInfo";
       requestJSON.message = msgJSON; 

       chrome.runtime.sendMessage(extensionsID, requestJSON,
            function (response) { 
                var result = new Object();                
                if(0 == response.errorcode){ 
                    result.error = 0;                     
                    result.value = response.result;                                     
                }
                else if(1 == response.errorcode){
                    result.error = response.errorcode;  
                    result.value = hostErr;
                }
                else{
                    result.error = response.errorcode;  
                    result.value = "sendMessage response error!";
                }
                callback(result);
	        }); 
}


nmCryptokit.prototype.SignMsgPKCS7 = function (bstrSourceData, bstrHashAlg, bWithSourceData, callback) {
       var requestJSON = new Object();
       var msgJSON = new Object();
       var paramArr = new Array();

       var source = new Object();
       source.param = bstrSourceData;
       paramArr.push(source);

       var selectedAlg = new Object();
       selectedAlg.param = bstrHashAlg;
       paramArr.push(selectedAlg);
      
       var IsWithSource = new Object();
       IsWithSource.param = bWithSourceData;
       paramArr.push(IsWithSource);

       msgJSON.function = "SignMsgPKCS7";
       msgJSON.params = paramArr;
       
       requestJSON.type = "SignMsgPKCS7";
       requestJSON.message = msgJSON; 

       chrome.runtime.sendMessage(extensionsID, requestJSON,
            function (response) { 
                var result = new Object();                
                if(0 == response.errorcode){ 
                    result.error = 0;                     
                    result.value = response.result;                                     
                }
                else if(1 == response.errorcode){
                    result.error = response.errorcode;  
                    result.value = hostErr;
                }
                else{
                    result.error = response.errorcode;  
                    result.value = "sendMessage response error!";
                }
                callback(result);
	        }); 
}


nmCryptokit.prototype.VerifyMsgSignaturePKCS7Attached = function (bstrSignature, bstrSignatureType, callback) {
          
       var requestJSON = new Object();
       var msgJSON = new Object();
       var paramArr = new Array();
       
       var Signature = new Object();
       Signature.param = bstrSignature;
       paramArr.push(Signature);
       
       var SignatureType = new Object();
       SignatureType.param = bstrSignatureType;
       paramArr.push(SignatureType);
                                            
       msgJSON.function = "VerifyMsgSignaturePKCS7Attached";
       msgJSON.params = paramArr;
       
       requestJSON.type = "VerifyMsgSignaturePKCS7Attached";
       requestJSON.message = msgJSON;           
       
       chrome.runtime.sendMessage(extensionsID, requestJSON,
            function (response) { 
                var result = new Object();                
                if(0 == response.errorcode){ 
                    result.error = 0;                     
                    result.value = response.result;                                     
                }
                else if(1 == response.errorcode){
                    result.error = response.errorcode;  
                    result.value = hostErr;
                }
                else{
                    result.error = response.errorcode;  
                    result.value = "sendMessage response error!";
                }
                callback(result);
	        });    
}


nmCryptokit.prototype.SignFilePKCS7Detached = function (bstrSourceData, bstrHashAlg, callback) {
       var requestJSON = new Object();
       var msgJSON = new Object();
       var paramArr = new Array();

       var source = new Object();
       source.param = bstrSourceData;
       paramArr.push(source);

       var selectedAlg = new Object();
       selectedAlg.param = bstrHashAlg;
       paramArr.push(selectedAlg);
      
       msgJSON.function = "SignFilePKCS7Detached";
       msgJSON.params = paramArr;
       
       requestJSON.type = "SignFilePKCS7Detached";
       requestJSON.message = msgJSON; 

       chrome.runtime.sendMessage(extensionsID, requestJSON,
            function (response) { 
                var result = new Object();                
                if(0 == response.errorcode){ 
                    result.error = 0;                     
                    result.value = response.result;                                     
                }
                else if(1 == response.errorcode){
                    result.error = response.errorcode;  
                    result.value = hostErr;
                }
                else{
                    result.error = response.errorcode;  
                    result.value = "sendMessage response error!";
                }
                callback(result);
	        }); 
}


nmCryptokit.prototype.VerifyFileSignaturePKCS7Detached = function (bstrSignature, bstrSignatureType, bstrSourceFile, callback) {
          
       var requestJSON = new Object();
       var msgJSON = new Object();
       var paramArr = new Array();
       
       var Signature = new Object();
       Signature.param = bstrSignature;
       paramArr.push(Signature);
       
       var SignatureType = new Object();
       SignatureType.param = bstrSignatureType;
       paramArr.push(SignatureType);
          
       var SourceFile = new Object();
       SourceFile.param = bstrSourceFile;
       paramArr.push(SourceFile);
                                      
       msgJSON.function = "VerifyFileSignaturePKCS7Detached";
       msgJSON.params = paramArr;
       
       requestJSON.type = "VerifyFileSignaturePKCS7Detached";
       requestJSON.message = msgJSON;           
       
       chrome.runtime.sendMessage(extensionsID, requestJSON,
            function (response) { 
                var result = new Object();                
                if(0 == response.errorcode){ 
                    result.error = 0;                     
                    result.value = response.result;                                     
                }
                else if(1 == response.errorcode){
                    result.error = response.errorcode;  
                    result.value = hostErr;
                }
                else{
                    result.error = response.errorcode;  
                    result.value = "sendMessage response error!";
                }
                callback(result);
	        });    
}


nmCryptokit.prototype.GetLastErrorDesc = function (callback) {
    var requestJSON = new Object();
    var msgJSON = new Object();
    var paramArr = new Array();

    msgJSON.function = "GetLastErrorDesc";
    msgJSON.params = paramArr;

    requestJSON.type = "GetLastErrorDesc";
    requestJSON.message = msgJSON;           

    chrome.runtime.sendMessage(extensionsID, requestJSON,
        function (response) {
            var result = new Object(); 
            if(1 != response.errorcode){ 
                result.error = response.errorcode; 
                result.value = response.result;
             }
            else{
                result.error = response.errorcode;                  
                result.value = hostErr;
            }
            callback(result);
	});
}