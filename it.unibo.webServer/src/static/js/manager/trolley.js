var current_trolley_cell = null
var status_trolley = document.getElementById("status_trolley");


const trolley_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/trolley/'
);

const trolleycontrol_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/trolleycontrol/'
);


document.getElementById("trolley_control").onclick =function () {
    var status = document.getElementById("status_trolley").innerHTML
    if(status == "STOPPED"){
        trolleycontrol_socket.send("{\"data\": \"ON\"}")
    }else{
        trolleycontrol_socket.send("{\"data\": \"OFF\"}")
    }
}


trolley_socket.onmessage = function (e) {
    console.log(e)
    const json_data = JSON.parse(e.data)

    status_trolley.innerHTML = json_data.state; 
    var coord = json_data.position.x.concat(json_data.position.y)
    colorTrolleyPosition(String(coord))

    if(json_data.state == "STOPPED"){
        document.getElementById("trolley_control").textContent = "ON"
    }else{
        document.getElementById("trolley_control").textContent  = "OFF"
    }
}

function colorTrolleyPosition(coord){
    console.log(coord)
    if(current_trolley_cell != null){
        console.log(current_trolley_cell)
        document.getElementById(current_trolley_cell).style.backgroundColor = "white"
    }
    document.getElementById(coord).style.backgroundColor = "#33FAFF"
    current_trolley_cell = coord
}