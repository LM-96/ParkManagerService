var status_outdoor = document.getElementById("status_outdoor");

const sonar_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/sonar/'
);

sonar_socket.onmessage = function (e) {
    console.log(e)

    const json_data = JSON.parse(e.data);
    console.log(json_data)

    status_outdoor.innerHTML = json_data;
}
