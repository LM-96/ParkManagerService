var current_robot_cell = null
var status_robot = document.getElementById("status_robot");


const robot_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/robot/'
);


robot_socket.onmessage = function (e) {
    console.log(e)
    const json_data = JSON.parse(e.data)
    console.log(json_data)

    status_robot.innerHTML = json_data.state; 
    var coord = json_data.position.x.concat(json_data.position.y)
    colorRobotPosition(String(coord))
}

function colorRobotPosition(coord){
    console.log(coord)
    if(current_robot_cell != null){
        console.log(current_robot_cell)
        document.getElementById(current_robot_cell).style.backgroundColor = "white"
    }
    document.getElementById(coord).style.backgroundColor = "#33FAFF"
    current_robot_cell = coord
}