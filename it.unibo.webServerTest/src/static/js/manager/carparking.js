var current_robot_cell = NaN

const carparking_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/carparking/'
);


carparking_socket.onmessage = function (e) {
    const data = JSON.parse(e.data)
    console.log(data)
}


function colorParkingSlot(slots){

}

function colorRobotPosition(coord){
    if(current_robot_cell != NaN){
        document.getElementById(current_robot_cell).style.backgroundColor = "white"
    }
    document.getElementById(coord).style.backgroundColor = "blue"
    current_robot_cell = coord
}