
// Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function(from, to) {
  var rest = this.slice((to || from) + 1 || this.length);
  this.length = from < 0 ? this.length + from : from;
  return this.push.apply(this, rest);
};

var items = [];

function sendNativeMessage (obj){
	console.log("Send ");
    console.log(JSON.stringify(obj));
	console.log(obj);
	chrome.runtime.sendNativeMessage('org.javafx.aria',
	  obj,
	  function(response) {
		console.log("Received " );
		console.log(response);
	  });
}

function escapeForPre(text) {
  return String(text).replace(/&/g, "&amp;")
                     .replace(/</g, "&lt;")
                     .replace(/>/g, "&gt;")
                     .replace(/"/g, "&quot;")
                     .replace(/'/g, "&#039;");
}

function getCookieString(cookie){
	var date = new Date(cookie.expirationDate);
    return  escapeForPre(cookie.domain)
        + "\t"
        + escapeForPre((!cookie.hostOnly).toString().toUpperCase())
        + "\t"
        + escapeForPre(cookie.path) 
        + "\t"     
        + escapeForPre(!cookie.secure.toString().toUpperCase())
        + "\t"     
        + escapeForPre(date.getTime() ? date.getTime() : "0")
        + "\t"
        + escapeForPre(cookie.name)
        + "\t"   
        + escapeForPre(cookie.value);

}

function sendToAria(c) {
	cookie_string = c.map(function(cookie){
            return getCookieString(cookie);
        }).join('\n');
		
    sendNativeMessage({
		'date':Date.now(),
		'id': id,
        'mime': mime,
        'fileSize':fileSize,
		'list': false,
		'page': ((typeof page !== 'undefined') ? page : false),
		'url': url,
		'origUrl': origUrl,
		'cookies': cookie_string,
		'referrer': referrer,
        'filename': filename, 
		'post': ((typeof postdata !== 'undefined') ? postdata : ""),
		'useragent': navigator.userAgent
	});
}

function UrlToAria(id, url, referrer,
                    filename, page, 
                    postdata, fileSize, mime)
{
	
	var origUrl = url;
	if(redirects.hasOwnProperty(url))
	{
		var newUrl = redirects[url];
		delete redirects[url];
		url = newUrl;
	}
    /*
    if( referrer ){
        chrome.cookies.getAll( {'url': referrer },
			function(c) {
				cookie_string = c.map(function(cookie){
		                return getCookieString(cookie);
		            }).join('\n');
				
		        sendNativeMessage({
					'date':Date.now(),
					'id': id,
		            'mime': mime,
		            'fileSize':fileSize,
					'list': false,
					'page': ((typeof page !== 'undefined') ? page : false),
					'url': url,
					'origUrl': origUrl,
					'cookies': cookie_string,
					'referrer': referrer,
		            'filename': filename, 
					'post': ((typeof postdata !== 'undefined') ? postdata : ""),
					'useragent': navigator.userAgent
				});
			}
		);
    }else{
    */
    
        chrome.cookies.getAll({ 'url': url },
			function(c) {
				cookie_string = c.map(function(cookie){
		                return getCookieString(cookie);
		            }).join('\n');
				
		        sendNativeMessage({
					'date':Date.now(),
					'id': id,
		            'mime': mime,
		            'fileSize':fileSize,
					'list': false,
					'page': ((typeof page !== 'undefined') ? page : false),
					'url': escapeForPre(url),
					'origUrl': origUrl,
					'cookies': cookie_string,
					'referrer': referrer,
		            'filename': filename, 
					'post': ((typeof postdata !== 'undefined') ? postdata : ""),
					'useragent': navigator.userAgent
				});
			}
		);
	/*
    }
    chrome.cookies.getAll({  },
			function(c) {
				cookie_string = c.map(function(cookie){
		                return getCookieString(cookie);
		            }).join('\n');
				
		        sendNativeMessage({
					'date':Date.now(),
					'id': id,
		            'mime': mime,
		            'fileSize':fileSize,
					'list': false,
					'page': ((typeof page !== 'undefined') ? page : false),
					'url': url,
					'origUrl': origUrl,
					'cookies': cookie_string,
					'referrer': referrer,
		            'filename': filename, 
					'post': ((typeof postdata !== 'undefined') ? postdata : ""),
					'useragent': navigator.userAgent
				});
			}
		);
    */
}



function UrlListToAria(urllist, referrer)
{
	chrome.cookies.getAll({ 'url': referrer },
		function(c) {
			cookie_string = c.map(function(cookie){
                    return getCookieString(cookie);
                }).join('\n');
			sendNativeMessage({
				'date':Date.now(),
				'id': -1,
				'list': true,
                'mime': '',
                'fileSize': -1,
				'url': urllist,
				'cookies': cookie_string,
				'referrer': referrer,
				'post': "",
				'useragent': navigator.userAgent
			});
		}
	);
}

chrome.downloads.onDeterminingFilename.addListener(
	function(downloadItem, suggest) {

		/*
		var size = downloadItem.totalBytes;		
		if(size != -1 && size < 2* 1204*1204)
		{
			suggest();
			return;
		}
		*/
		
		/*
		if(downloadItem.mime.search('image') !== -1){
			suggest();
			return;
		}
		*/
		
		if(downloadItem.mime.search('html') !== -1){
			suggest();
			return;
		}
		
		// workaround for MEGA.co.nz
		if(downloadItem.url.toLowerCase().indexOf("filesystem:") == 0)
		{
			suggest();
			return;
		}
		
		// user hit "cancel" in fdm
		/*
		if (cancelled.indexOf(downloadItem.url) != -1)
		{
			cancelled.remove(cancelled.indexOf(downloadItem.url));
			suggest();
			return;
		}
		*/

		items[downloadItem.id] = downloadItem.url;
		chrome.downloads.cancel(downloadItem.id, function () {
			//chrome.downloads.erase({ id: downloadItem.id })
		});
		
		UrlToAria(downloadItem.id, downloadItem.url, downloadItem.referrer, 
				  downloadItem.filename, false, null, downloadItem.fileSize, downloadItem.mime);
		console.log(downloadItem);
		return true;
	}
);


function createContextMenu()
{

		chrome.contextMenus.create({
			"id": "this",
			"title": "Download link with Aria",
			"contexts": [ "image", "link" ],
			"onclick" :
				function(info, tab) {
					if(info.linkUrl)
						chrome.downloads.download({ url: info.linkUrl, saveAs: false });
					else if(info.mediaType === "image"){
						chrome.downloads.download({ url: encodeURI(info.srcUrl), saveAs: true });
                        //UrlToAria(-1, encodeURI(e.srcUrl), tab.url, '', true, -1, '');
                    }
				}
		});
        
        chrome.contextMenus.create({
			"id": "video",
			"title": "Download This Video",
			"contexts": [ "video", "audio" ],
			"onclick" :
				function (info, tab) {
				  //console.log("item " + info.menuItemId + " was clicked");
				  //console.log("info: " + JSON.stringify(info));
				  //console.log("tab: " + JSON.stringify(tab));
				  chrome.downloads.download({ url: info.srcUrl, saveAs: false });
				  //UrlToAria(-1, encodeURI(info.srcUrl), tab.url, tab.title, false, '', -1, '');
				}
		});
        
		chrome.contextMenus.create({
			"id": "all",
			"title": "Download All",
			"contexts": [ "page" ]
		});

		chrome.contextMenus.create({
			"id": "selected",
			"title": "Download Selected",
			"contexts": [ "selection" ]
		});

	chrome.contextMenus.onClicked.addListener(
		function(info, tab) {
			switch(info.menuItemId)
			{
				case "all":
					chrome.tabs.executeScript(
						tab.id, 
						{ "code":
							"JSON.stringify([].map.call(document.getElementsByTagName('a'), function(n) {return n.href;}).concat([].map.call(document.getElementsByTagName('img'), function(n) {return n.src;})));"
						},
						function(h) {
							UrlListToAria(eval(h[0]).join('\n'), tab.url);
						}
					);
					break;

				case "selected":
					chrome.tabs.executeScript(
						tab.id, 
						{ "code":
							"var s = document.getSelection(); var sel = s.getRangeAt(0).cloneContents(); var dv =document.createElement('div'); dv.appendChild(sel); try{ var u = s.anchorNode.data; var url = new URL(u); var link =document.createElement('a'); link.href = url.href; dv.appendChild(link); }catch(e){ } JSON.stringify([].map.call(dv.getElementsByTagName('a'), function(n) {return n.href;})); "
						},
						function(h) {
							UrlListToAria(eval(h[0]).join('\n'), tab.url);
						}
					);
					break;

				case "page":
				 	chrome.downloads.download({ url: tab.url, saveAs: false });
					//UrlToAria(-1, tab.url, tab.url, null, true, null,-1, null);
					break;

				default:
					break;
			}
		}
	);
}

// special processing for redirects & POST requests returning files (like rutracker.org)
//

var redirects = [];
var request_bodies  = [];
var request_headers = [];

chrome.webRequest.onBeforeRequest.addListener(
	function(details) {
		if(details.method == "POST")
		{
			request_bodies[details.requestId] = "&";
			if(undefined != details.requestBody && undefined != details.requestBody.formData)
			{
				for(var field in details.requestBody.formData)
				{
					for(var i = 0; i < details.requestBody.formData[field].length; ++i)
						request_bodies[details.requestId] +=
							(field +
							"=" +
							encodeURIComponent(details.requestBody.formData[field][i]) +
							"&");
				}
			}
			//  no need to process details.requestBody.raw here.
		}
	},
	{urls: ["<all_urls>"]},
	["requestBody"]
);

chrome.webRequest.onSendHeaders.addListener(
	function(details) {
		if(details.method == "POST")
			request_headers[details.requestId] = details.requestHeaders;
	},
	{urls: ["<all_urls>"]},
	["requestHeaders"]
);

chrome.webRequest.onHeadersReceived.addListener(
	function(details) {
		if((details.statusLine.indexOf("301") != -1 || details.statusLine.indexOf("302") != -1))
		{
			var newUrl = "";
			for(var i = 0; i < details.responseHeaders.length; ++i)
			{
				if(details.responseHeaders[i].name.toLowerCase()=="location")
				{
					newUrl = details.responseHeaders[i].value;
					break;
				}
			}
			if(newUrl != "")
				redirects[details.url] = newUrl;
		}
		if(details.method == "POST")
		{
			var result;
			var file = false;
			if(details.type != "xmlhttprequest")
				for(var i = 0; i < details.responseHeaders.length; ++i)
				{
					if(details.responseHeaders[i].name.toLowerCase()=="content-disposition")
						file = true;
					// prevent AJAX from breaking
					if(details.responseHeaders[i].name.toLowerCase()=="content-type" &&
						(details.responseHeaders[i].value.toLowerCase().indexOf("json") != -1 ||
						details.responseHeaders[i].value.toLowerCase().indexOf("text") != -1 ||
						details.responseHeaders[i].value.toLowerCase().indexOf("javascript") != -1))
					{
						file = false;
						break;
					}
				}
			if(file)
			{
				var referrer = "";
				for(var j = 0; j < request_headers[details.requestId].length; ++j)
				{
					var rheader = request_headers[details.requestId][j];
					if(rheader.name.toLowerCase()=="referrer" ||
							rheader.name.toLowerCase()=="referer")
						referrer = rheader.value;
				}
				
				UrlToAria(-1, details.url, referrer, null, false,
					request_bodies[details.requestId], null, null);
				chrome.tabs.update( { 'url': referrer });
				console.log(details);
				result = { 'cancel': true };
			}
			request_bodies.remove(details.requestId);
			request_headers.remove(details.requestId);
			return result;
		}
	},
	{urls: ["<all_urls>"]},
	["blocking", "responseHeaders"]
);

createContextMenu();


