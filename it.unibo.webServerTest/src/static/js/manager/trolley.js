var current_trolley_cell = null
var status_trolley = document.getElementById("status_trolley");


const trolley_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/trolley/'
);


trolley_socket.onmessage = function (e) {
    console.log(e)
    const json_data = JSON.parse(e.data)
    console.log(json_data)

    status_trolley.innerHTML = json_data.state; 
    var coord = json_data.position.x.concat(json_data.position.y)
    colorTrolleyPosition(String(coord))
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