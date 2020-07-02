package com.NowakArtur97.GlobalTerrorismAPI.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNode extends Node {

    private String userName;
    private char[] password;
}