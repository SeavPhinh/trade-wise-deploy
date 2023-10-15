package com.example.shopservice.response;

import com.example.shopservice.enumeration.Level;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {

    private UUID userId;
    private Level level;
    private UUID shopId;

}
