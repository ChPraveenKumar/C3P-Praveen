$(document).ready(function() {
  $("div.abc.xyz").width("200px");
   
});

function dateTimePicker($){
	jQuery( "#datetimepicker1" ).datetimepicker();
}

function subnetMaskValidation(event){
	var maskValue = event.target.value;
	var mask = event.target.value;
	var maskId = event.target.id;
	maskValidation(maskValue);
	function maskValidation(maskValue){
		switch(maskValue) {
	    case "0.0.0.0":
	    	maskValue = true;
	    	 break;
	    case "128.0.0.0":
	    	 maskValue = true;
	    	 break;
	    case "192.0.0.0":
	    	 maskValue = true;
	    	 break;
	    case "224.0.0.0":
	    	 maskValue = true;
	    	 break;
	    case "240.0.0.0":
	    	 maskValue = true;
	    	 break;
	    case "248.0.0.0":
	    	 maskValue = true;
	    	 break;
	    case "252.0.0.0":
	    	 maskValue = true;
	    	 break;
	    case "254.0.0.0":
	    	 maskValue = true;
	    	 break;
	    case "255.0.0.0":
	    	 maskValue = true;
	    	 break;
	    case "255.128.0.0":
	    	 maskValue = true;
	    	 break;
	    case "255.192.0.0":
	    	 maskValue = true;
	    	 break;
	    case "255.224.0.0":
	    	 maskValue = true;
	    	 break;
	    case "255.240.0.0":
	    	 maskValue = true;
	    	 break;
	    case "255.248.0.0":
	    	 maskValue = true;
	    	 break;
	    case "255.252.0.0":
	    	 maskValue = true;
	    	 break;
	    case "255.254.0.0":
	    	 maskValue = true;
	    	 break;
	    case "255.255.0.0":
	    	 maskValue = true;
	    	 break;
	    case "255.255.128.0":
	    	 maskValue = true;
	    case "255.255.192.0":
	    	 maskValue = true;
	    	 break;
	    case "255.255.224.0":
	    	 maskValue = true;
	    	 break;
	    case "255.255.240.0":
	    	 maskValue = true;
	    	 break;
	    case "255.255.248.0":
	    	 maskValue = true;
	    	 break;
	    case "255.255.252.0":
	    	 maskValue = true;
	    	 break;
	    case "255.255.254.0":
	    	 maskValue = true;
	    	 break;
	    case "255.255.255.0":
	    	 maskValue = true;
	    	 break;
	    case "255.255.255.128":
	    	 maskValue = true;
	    	 break;
	    case "255.255.255.192":
	    	 maskValue = true;
	    	 break;
	    case "255.255.255.224":
	    	 maskValue = true;
	    	 break;
	    case "255.255.255.240":
	    	 maskValue = true;
	    	 break;
	    case "255.255.255.248":
	    	 maskValue = true;
	    	 break;
	    case "255.255.255.252":
	    	 maskValue = true;
	    	 break;
	    case "255.255.255.254":
	    	 maskValue = true;
	    	 break;
	    case "255.255.255.255":
	    	 maskValue = true;
	    	 break;
	    default:
	    	maskValue = false;
	    }
		if(mask == ""){
			$("#"+ maskId).css("background-color", "");
		}
		if(maskValue == true){
			$("#"+ maskId).removeClass("ng-invalid");
			$("#"+ maskId).css("background-color", "rgba(234, 193, 26, 0.12)");
		}else{
			$("#"+ maskId).removeClass("ng-valid");
			$("#"+ maskId).addClass("ng-invalid");
			$("#"+ maskId).css("background-color", "#FFCCCC;");
			
		}
	}
}

function openAddPopUp(popUpId){
	$("#" + popUpId).css("display", "block");
}
function closeAddPopUp(popUpId){
	$("#" + popUpId).css("display", "none");
}

function helpPopUp(){
	bootbox.alert({ 
		  
		  title: "Help",
		  message: "Work In Progress", 
		  callback: function(){ /* your callback code */ }
		})
  }

function alertPopUp(header, messege){
	bootbox.alert({ 
		  
		  title: header,
		  message: messege, 
		  callback: function(){ /* your callback code */ }
		})
			
  }
function dragElement(elmnt) {
    var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
    if (document.getElementById(elmnt.id+'Header')) {
      /* if present, the header is where you move the DIV from:*/
      document.getElementById(elmnt.id+'Header').onmousedown = dragMouseDown;
    } else {
      /* otherwise, move the DIV from anywhere inside the DIV:*/
      elmnt.onmousedown = dragMouseDown;
    }

    function dragMouseDown(e) {
      e = e || window.event;
      // get the mouse cursor position at startup:
      pos3 = e.clientX;
      pos4 = e.clientY;
      document.onmouseup = closeDragElement;
      // call a function whenever the cursor moves:
      document.onmousemove = elementDrag;
    }

    function elementDrag(e) {
      e = e || window.event;
      // calculate the new cursor position:
      pos1 = pos3 - e.clientX;
      pos2 = pos4 - e.clientY;
      pos3 = e.clientX;
      pos4 = e.clientY;
      // set the element's new position:
      elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
      elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";
    }

    function closeDragElement() {
      /* stop moving when mouse button is released:*/
      document.onmouseup = null;
      document.onmousemove = null;
    }
  }
