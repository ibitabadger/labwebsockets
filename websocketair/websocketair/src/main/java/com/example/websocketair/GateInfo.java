package com.example.websocketair;

import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class GateInfo {
    private String gate;
    private String flightNumber;
    private String Destination;
    private String departureTime;
    private String status;
}
