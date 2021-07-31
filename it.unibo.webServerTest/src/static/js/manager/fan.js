var status_fan = document.getElementById("status_fan");

const fan_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/fan/'
);

const fancontrol_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/fancontrol/'
);


fan_socket.onmessage = function (e) {
    console.log(e.data)

    status_fan.innerHTML = e.data;
}

document.getElementById("fan_control").onclick =function () {
            console.log("TUA MAMMA  ")
            var status = document.getElementById("status_fan").innerHTML
            if(status == "ON"){
                fancontrol_socket.send("{\"data\": \"OFF\"}")
            }else if(status == "OFF"){
                fancontrol_socket.send("{\"data\": \"ON\"}")
            }
        }

