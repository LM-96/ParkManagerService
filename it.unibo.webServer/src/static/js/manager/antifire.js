var status_antifire = document.getElementById("status_antifire");

const antifire_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/antifire/'
);

const antifirecontrol_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/antifirecontrol/'
);


antifire_socket.onmessage = function (e) {
    console.log(e.data)
    const json_data = JSON.parse(e.data)


    status_antifire.innerHTML = json_data.mode;

    if(json_data.mode == "MANUAL"){
        document.getElementById("antifire_control").textContent = "AUTO"
    }else{
        document.getElementById("antifire_control").textContent  = "MANUAL"
    }

    if (json_data.temp == "CRITICAL"){
        alert("The temperature reached a critical level!!\nThe antifire is on " + json_data.mode)
    }
}



document.getElementById("antifire_control").onclick =function () {
            var status = document.getElementById("status_antifire").innerHTML
            if(status == "MANUAL"){
                antifirecontrol_socket.send("{\"data\": \"AUTO\"}")
            }else{
                antifirecontrol_socket.send("{\"data\": \"MANUAL\"}")
            }
        }

