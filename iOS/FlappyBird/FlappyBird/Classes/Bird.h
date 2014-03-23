//
//  Bird.h
//  FlappyBird
//
//  Created by Hai Do Minh on 3/7/14.
//  Copyright (c) 2014 Hai Do Minh. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "cocos2d.h"

typedef enum {
    BIRD_STATE_STAND,
    BIRD_STATE_JUMP,
    BIRD_STATE_DOWN,
    BIRD_STATE_DIE,
    BIRD_STATE_PAUSE
} BirdState;

typedef enum {
    BIRD_TYPE_YELLOW,
    BIRD_TYPE_BLUE,
    BIRD_TYPE_RED
} BirdType;

@interface Bird : NSObject
@property BirdState state;
@property CCSprite* sprBird;
@property BirdType type;
@property CCScene* scene;

-(id) initWithType:(BirdType)pType position:(CGPoint)pPos scene:(CCScene*)pScene;
//-(void)setState:(BirdState)state;
//-(BirdState)getState;

@end
