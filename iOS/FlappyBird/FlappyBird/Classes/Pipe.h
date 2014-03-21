//
//  Pipe.h
//  FlappyBird
//
//  Created by Hai Do Minh on 3/21/14.
//  Copyright (c) 2014 Hai Do Minh. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "cocos2d.h"

typedef enum {
    RED,
    BLUE
} PipeType;

@interface Pipe : NSObject
@property PipeType type;
@property CCSprite* sprTop;
@property CCSprite* sprBottom;
@property CCScene* scene;

-(id)initWithType:(PipeType)pType posX:(float)pX topY:(float)pTop range:(float)pRange scene:(CCScene*)pScene;
@end
