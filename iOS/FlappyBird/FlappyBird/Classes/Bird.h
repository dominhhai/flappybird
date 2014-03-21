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
    STAND,
    JUMP,
    DOWN,
    DIE,
    PAUSE
} BirdState;

typedef enum {
    RED,
    BLUE,
    YELLOW
} BirdType;

@interface Bird : NSObject
@property BirdState state;
@property CCSprite* sprBird;
@property BirdType type;
@property CCScene* scene;

-(id) initWithType:(BirdType)pType position:(CGPoint)pPos scene:(CCScene*)pScene;
-(void)setState:(BirdState)state;

@end
