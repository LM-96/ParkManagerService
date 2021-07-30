var status_outdoor = document.getElementById("status_oudoor");

const sonar_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/sonar/'
);

sonar_socket.onmessage = function (e) {
    const json_data = JSON.parse(e.data);
    console.log(json_data.data)
    const data = json_data.data
    status_outdoor.innerHTML = data;
}
