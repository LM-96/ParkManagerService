
var init_state = "OFF"
var output_fan = document.getElementById("value_fan");
output_fan.innerHTML = init_state;

const fan_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/ws/fan/'
);


fan_socket.onmessage = function (e) {
    const data = JSON.parse(e.data);
    console.log(data)
    document.querySelector("#value_fan").innerHTML = (data.data)
}

