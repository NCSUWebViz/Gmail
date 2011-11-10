/**
 * returns the importance for each contact
 * 
 */
var data;
function getImportance(){
	console.log("called")
	$.ajax({
		type:'GET',
		url:"/GmailViz/sendcontact",
		success: function(msg){
			renderTable(msg)
		},
		error: function(msg){
			alert('Failed');
		}
	});
}

function renderTable(msg) {
        // get the reference for the body
		console.log(msg)
		msg = eval('(' + msg + ')');
		data = msg;
		console.log(data)
        var body = document.getElementsByTagName("body")[0];

        // creates a <table> element and a <tbody> element
        var tbl     = document.createElement("table");
        var tblBody = document.createElement("tbody");
        var radioButton ="";
        var element;
        // creating all cells
        var i=0;
        var row;
        var cell;
        var cellText;
        for (var j = 0; j < data.IndividualContactImportance.length; j++) {
            // creates a table row
            	row = document.createElement("tr");
                // Create a <td> element and a text node, make the text
                // node the contents of the <td>, and put the <td> at
                // the end of the table row
                cell = document.createElement("td");
                cellText = document.createTextNode(data.IndividualContactImportance[j].email_id);
                cell.appendChild(cellText);
                row.appendChild(cell);
                cell = document.createElement("td");
                element = document.createElement("div");
                radioButton ="Very Important <input name=group"+(j+1)+" type=radio value=10 />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                radioButton+="Important <input name=group"+(j+1)+" type=radio value=7 />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                radioButton+="Less Important <input name=group"+(j+1)+" type=radio value=4  />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                radioButton+="Least Important <input name=group"+(j+1)+" type=radio value=1 />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                element.innerHTML = radioButton;
                cell.appendChild(element);
                row.appendChild(cell);
            // add the row to the end of the table body
            tblBody.appendChild(row);
            i=j;
        }
        for(var j=0;j<data.GroupedContactImportance.length;j++){
        	row = document.createElement("tr");
        	console.log(i)
            // Create a <td> element and a text node, make the text
            // node the contents of the <td>, and put the <td> at
            // the end of the table row
            cell = document.createElement("td");
            cellText = document.createTextNode(data.GroupedContactImportance[j].email_id);
            cell.appendChild(cellText);
            row.appendChild(cell);
            cell = document.createElement("td");
            element = document.createElement("div");
            radioButton ="Very Important <input name=group"+(i+1)+" type=radio value=10 />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            radioButton+="Important <input name=group"+(i+1)+" type=radio value=7 />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            radioButton+="Less Important <input name=group"+(i+1)+" type=radio value=4  />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            radioButton+="Least Important <input name=group"+(i+1)+" type=radio value=1 />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            element.innerHTML = radioButton;
            cell.appendChild(element);
            row.appendChild(cell);
            i++;
        // add the row to the end of the table body
        tblBody.appendChild(row);
        }

        // put the <tbody> in the <table>
        tbl.appendChild(tblBody);
        // appends <table> into <body>
        body.appendChild(tbl);
        element = document.createElement("div");
        var submit = "<input type='Submit' value='Submit' onClick='setImportance();' />";
        element.innerHTML = submit;
        body.appendChild(element);
        // sets the border attribute of tbl to 2;
        tbl.setAttribute("id", "rounded-corner");
}

function setImportance(){
	var obj = [];
	console.log("inside setImpotance:" + data)
	var i=0;
	var obj = "";
	for(var j=0 ;j < data.IndividualContactImportance.length; j++){
		key = data.IndividualContactImportance[j].email_id;
		value = $('input[name=group'+(j+1)+']:checked').val();
		console.log(key+':'+value)
		obj += key+":"+value+";";
		i=j;
	}
	for(var j=0 ;j < data.GroupedContactImportance.length; j++){
		key = data.GroupedContactImportance[j].email_id;
		value = $('input[name=group'+(i+1)+']:checked').val();
		console.log(key+':'+value)
		obj += key+":"+value+";";
		i++;
	}
	
	console.log(obj)
	$.ajax({
		type:'GET',
		url:"/GmailViz/contact",
		data: {obj:obj},
		success: function(msg){
			alert('success');
			window.location = "http://localhost:8080/GmailViz/user_importance.jsp"
		},
		error: function(msg){
			alert('Failed');
		}
	});
}





