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
    console.log(e.mode)

    status_antifire.innerHTML = e.mode;

    if(e.mode == "MANUAL"){
        document.getElementById("antifire_control").textContent = "AUTO"
    }else{
        document.getElementById("antifire_control").textContent  = "MANUAL"
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

