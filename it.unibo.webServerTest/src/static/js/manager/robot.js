var current_robot_cell = NaN
var status_robot = document.getElementById("status_robot");


const robot_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/robot/'
);


robot_socket.onmessage = function (e) {
    console.log(e)

    status_robot.innerHTML = e.data; 
}

function colorRobotPosition(coord){
    if(current_robot_cell != NaN){
        document.getElementById(current_robot_cell).style.backgroundColor = "white"
    }
    document.getElementById(coord).style.backgroundColor = "blue"
    current_robot_cell = coord
}