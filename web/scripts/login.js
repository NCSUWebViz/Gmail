var selected, viewer, divs = {};
var nav = document.getElementById('nav');
var data = new Array();
var userName, password;

Ext.onReady(function() {
    Ext.QuickTips.init();
	var sizeArray = new Array();
	var redirect = 'imp_contact.jsp';
    var loginForm = new Ext.FormPanel({
    	id:'loginForm',
        title:'Please Login', 
        defaultType:'textfield',
        monitorValid:true,
        items:[{ 
                fieldLabel:'Username', 
                name:'loginUsername', 
                allowBlank:false
            },{ 
                fieldLabel:'Password', 
                name:'loginPassword', 
                inputType:'password', 
                allowBlank:false
            }],
        buttons:[{ 
                text:'Login',
                formBind: true,	 
                handler:function(){ 
                    loginForm.getForm().submit({ 
                        method:'POST',
                        url: '/GmailViz/home',
                        waitTitle:'Connecting', 
                        waitMsg:'Sending data...',
                        success:function(form, action) {
                        	console.log(action.response.responseText);
                        	
                        	var userNameBox = Ext.getCmp("loginForm").getForm().findField("loginUsername");
                        	var passwordBox = Ext.getCmp("loginForm").getForm().findField("loginPassword");
                       	
                        	userName = userNameBox.getValue();
                        	password = passwordBox.getValue();
                        	
                        	var JSONObject = eval('(' + action.response.responseText + ')');
                        	data = JSONObject;
                        	var formWindow = Ext.getCmp('formWindow');
                        	formWindow.close();
                        	window.location = redirect;
                        },
                        failure:function(form, action){ 
                            	Ext.Msg.alert("failure : " + action.response.responseText)
                        },
                        callback: function(form, action) {
                        	Ext.Msg.alert(response.responseText);
                        }
                    }); 
                } 
            }]
    });
 
    var win = new Ext.Window({
    	id: 'formWindow',
        layout:'fit',
        width:300,
        height:150,
        closable: false,
        resizable: false,
        plain: true,
        border: false,
        items: [loginForm]
	});
    win.show();
});

//function intialize() {
//	//buildNav(data);
//	drawMailBoxes(data);
//	for(var i in data) {
//		if (data[i].folderName == "INBOX") {
//			loadProject(data[i]);
//			break;
//		}
//	}
	
	//window.addEventListener( 'resize', updateLayout, false );
	//window.addEventListener( 'popstate', function ( event ) {
		//if ( event.state != null ) loadProject( event.state );
	//}, false );
//}

//function buildNav(projects) {
//	for (var i in projects) {
//		console.log(projects[i].imageSrc);
//		nav.appendChild(buildProject(projects[i]));
//	}
//}
//
//function buildProject( data ) {
//	var div = document.createElement( 'div' );
//	div.className = 'project';
//	div.addEventListener( 'click', function ( event ) {
//		loadProject( data );
//		//history.pushState( data, data.name, '/' + data.id + '/' + data.name.replace( /\ /gi, '_' ).replace( /[:.,\'()%]/gi, '' ) );
//	
//	}, false );
//	
//	var img = document.createElement( 'img' );
//	img.width = 150;
//	img.height = 150;
//	img.src = data.imageSrc;
//	img.style.border = '2px solid #ffffff';
//	img.style.marginRight = '10px';
//	img.style.backgroundColor = '#000000';
//	img.style.cssFloat = 'left';
//	div.appendChild(img);
//	
//	var text = document.createElement('div');
//	text.innerText = data.folderName + "(" + data.folderSize + ")";
//	text.style.width = '160px';
//	text.style.color = '#ffffff';
//	text.style.fontWeight = 'bold';
//	text.style.fontSize = '14px';
//	div.appendChild(text);
//	
//	divs[data.folderName] = div;
//	
//	return div;
//}

//function loadProject( data ) {
//
//	if (selected) {
//		selected.className = 'project';
//		document.body.removeChild( viewer );
//	}
//
//	selected= divs[data.folderName];
//	selected.className = 'project selected';
//
//	document.title = 'Email | ' + data.folderName;
//
//	viewer = document.createElement('iframe');
//	viewer.src = "/GmailViz/" + data.folderName + ".jsp";
//	document.body.appendChild(viewer);
//
//	updateLayout();
//}

//function updateLayout( event ) {
//
//	viewer.style.width = window.innerWidth + 'px';
//	viewer.style.height = ( window.innerHeight - 61 ) + 'px';
//
//}

//var datainfo = {"NCSU": [{"Period":"Today","folderSize":"56"}],"success": "true"} 
//var paper = new Raphael(document.getElementById('nav'), document.getElementById('nav').width, document.getElementById('nav').height); //initialize the canvas...
//var text = paper.text(350,50,' '); //initialize the text to be displayed...
//var text1 = paper.text(350, 10, ' ');
//var cwidth = 0;
//var x=100;
//var y=100;
//var flag=true;
//function postMessage(a){
//	var rot=0;
//	text.attr('text',a);
//	text.attr({
//		'font-size': '20px'
//	});
//	text.attr({transform: "r" + rot});
//	text.attr({transform: "r" + rot / 1.5});
//    text.attr({transform: "r" + rot / 2});
//} //used by Event_ hover as handler function
//function postData(a){
//	text1.attr('text',a);
//	text1.attr({
//		'font-size': '20px'
//	});
//	this.animate(anim.delay(500));
//} //used by Event_ hover as handler function

//function drawMailBoxes(JSONObject){
//	var data1 = JSONObject;
//	$("profile").text(userName);
//	console.log(userName)
//	console.log(data)
//	var anim = Raphael.animation({cx: 10, cy: 20}, 2e3);
//	for (i=0;i<data1.folderSizes.length;i++){
//		if(i>data1.folderSizes.length/2 && flag!=false){
//			cwidth=75;
//			y=200;
//			flag=false;
//			console.log(flag);
//		}
//		var button = paper.image("./resources/images/"+data.folderSizes[i].folderName+".png",x+cwidth,y,80,100);
//		button.animate(anim, 500);
//		button.attr({
//			cursor:'pointer'
//		});
//		button.data("Name",data1.folderSizes[i].folderName);
//		button.data("Size",data1.folderSizes[i].folderSize);
//		button.data("Period",datainfo.NCSU[0].Period);
//		button.data("fSize",datainfo.NCSU[0].folderSize);
//		button.hover(function () {
//				document.title = this.data("Name");
//				this.animate({width: 120, height: 150}, 2000, 'bounce');
//				postMessage('[ '+this.data("Name")+' has '+this.data("Size")+ ' mail(s)]');
//				$.ajax({
//					  type: 'POST',
//					  url: '/GmailViz/folder',
//					  data: "folderName="+this.data("Name")+"&password="+password+"&userName="+userName,
//					  success:function(msg){
//						  console.log(msg)
//						  
//					  },
//					  
//					});
//        	},function () {
//        		document.title='login';
//        		this.animate({width: 80, height: 100}, 2000, 'bounce');
//        		postMessage(' ');
//		});
//		button.click(function(){
//		});
//		cwidth+=150;
//    }
//	
//}